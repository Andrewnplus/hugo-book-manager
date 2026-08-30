package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.model.MigrateLeafRepoInput
import com.nplus.bookmanager.model.MigrateLeafRequest
import com.nplus.bookmanager.model.MigrateLeafResponse
import com.nplus.bookmanager.model.MigrateLeafResult
import com.nplus.bookmanager.model.RepoIndex
import com.nplus.bookmanager.service.GitHubCliService
import com.nplus.bookmanager.service.MigrationPlanner
import com.nplus.bookmanager.service.RepoIndexService
import com.nplus.bookmanager.service.RepoPlan
import com.nplus.bookmanager.util.CliFormatter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class MigrateTopicTiersCommand(
    private val repoIndexService: RepoIndexService = RepoIndexService(),
    private val ghService: GitHubCliService = GitHubCliService(),
) : CliktCommand(name = "migrate-topic-tiers") {
    override fun help(context: Context) = "Migrate book repos from 2-tier (top/sub) to 3-tier (top/sub/leaf) topics + folder layout"

    private val apply by option("--apply", help = "Actually apply changes (gh edit + folder move)")
        .flag(default = false)

    private val batchSize by option("--batch", help = "Repos per batch in apply mode (default 20)")
        .int()
        .default(20)

    private val limit by option("--limit", help = "Cap total repos processed (default unlimited)")
        .int()
        .default(Int.MAX_VALUE)

    private val onlyName by option("--name", help = "Only process this single repo name")

    private val extraWorkDir by option(
        "--extra-work-dir",
        help =
            "Additional directory to scan for local clones. " +
                "By default ONLY 'books-done' (sibling of DEFAULT_WORK_DIR) is scanned. " +
                "DEFAULT_WORK_DIR itself (typically new-books) is intentionally skipped " +
                "so in-progress books aren't moved out from under your IDE/git state.",
    )

    private val json =
        Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            isLenient = true
        }

    override fun run() {
        CliFormatter.printHeader("Migrate Topic Tiers (top → sub → leaf)")
        if (apply) runApply() else runProposal()
    }

    private fun runProposal() {
        val index = repoIndexService.load()
        if (index.repos.isEmpty()) {
            println("Repo index is empty. Run `./gradlew refreshRepoIndex` first.")
            return
        }

        val needingMigration =
            index.repos
                .filter { MigrationPlanner.needsMigration(it) }
                .let { list -> if (onlyName != null) list.filter { it.name == onlyName } else list }
        val pending = needingMigration.take(limit)

        if (pending.isEmpty()) {
            println("✅ Nothing to migrate. All repos in scope already have a leaf-* topic.")
            return
        }

        val alreadyMigrated = index.repos.size - needingMigration.size
        println("\n📊 Migration scope")
        println("  Total repos in index:  ${index.repos.size}")
        println("  Already 3-tier:        $alreadyMigrated")
        println("  Need migration:        ${needingMigration.size}")
        println("  Included this run:     ${pending.size}")
        if (onlyName != null) println("  Filter --name:         $onlyName")
        if (limit != Int.MAX_VALUE) println("  Limit:                 $limit")

        val request =
            MigrateLeafRequest(
                promptFile = "templates/prompts/migrate-leaf.txt",
                taxonomyFile = "templates/topic-taxonomy.yaml",
                repos =
                    pending.map {
                        MigrateLeafRepoInput(
                            name = it.name,
                            description = it.description,
                            currentTopics = it.topics,
                        )
                    },
            )

        val inputDir = File("ai-tasks/input").also { it.mkdirs() }
        val requestFile = File(inputDir, "migrate-leaf-request.json")
        requestFile.writeText(json.encodeToString(request))

        println("\n📝 Wrote request: ${requestFile.path}")
        println()
        println("Next steps:")
        println("  1. Ask Claude Code to process the migration request:")
        println("       \"請處理 migrate-leaf 任務\"")
        println("  2. Claude should consult `templates/topic-taxonomy.yaml` and write")
        println("     `ai-tasks/output/migrate-leaf-response.json` with each repo's")
        println("     (topCategory, subCategory, leafCategory) triple.")
        println("  3. Review the response (and edit topic-taxonomy.yaml to merge any")
        println("     similar new leafs Claude proposed).")
        println("  4. Apply with: ./gradlew migrateTopicTiers -Papply=true")
        println()
    }

    private fun runApply() {
        if (!ghService.checkPrerequisites()) return

        val response = readResponse() ?: return
        val index = repoIndexService.load()
        val byName = index.repos.associateBy { it.name }

        val owner =
            AppConfig.githubUsername.takeIf { it.isNotBlank() }
                ?: ghService.getUsername()
        if (owner.isNullOrBlank()) {
            println("Error: cannot determine repo owner (set GITHUB_USERNAME in local.properties).")
            return
        }

        val cloneIndex = buildLocalCloneIndex()
        val scannedRoots = workRoots().joinToString(", ") { it.absolutePath }
        println("\n🔍 Indexed ${cloneIndex.size} local clones in: $scannedRoots")
        println("   (DEFAULT_WORK_DIR / new-books is intentionally NOT scanned — in-progress books stay put.)")

        val plans =
            response.results
                .let { list -> if (onlyName != null) list.filter { it.name == onlyName } else list }
                .take(limit)
                .mapNotNull { result ->
                    val current = byName[result.name]
                    if (current == null) {
                        println("  ⚠ Skipping ${result.name}: not in repo index")
                        null
                    } else {
                        MigrationPlanner.buildPlan(
                            current,
                            result,
                            cloneIndex,
                            workRoots(),
                            File(AppConfig.defaultWorkDir),
                        )
                    }
                }

        if (plans.isEmpty()) {
            println("✅ Nothing to apply.")
            return
        }

        val noOps = plans.count { it.isNoOp() }
        val withChanges = plans.size - noOps
        println("\n📊 Plan summary")
        println("  Repos to change:  $withChanges")
        println("  No-op (already correct): $noOps")
        if (noOps == plans.size) {
            println("✅ Everything already matches target. Nothing to do.")
            return
        }

        val failedLog = File("ai-tasks/output/migrate-failed.log").apply { parentFile?.mkdirs() }
        failedLog.writeText("# migrate-topic-tiers failures (started ${nowStamp()})\n")

        plans.filter { !it.isNoOp() }.chunked(batchSize).forEachIndexed { batchIdx, batch ->
            println()
            CliFormatter.printDivider(60)
            println("Batch ${batchIdx + 1} (${batch.size} repos)")
            CliFormatter.printDivider(60)
            batch.forEach { printPlan(it) }

            print("\nApply this batch? [Enter to apply / s to skip / q to abort]: ")
            when (readlnOrNull()?.trim()?.lowercase()) {
                "q", "quit", "abort" -> {
                    println("Aborted by user.")
                    return
                }

                "s", "skip" -> {
                    println("Skipped batch ${batchIdx + 1}.")
                    return@forEachIndexed
                }
            }

            batch.forEach { plan -> executePlan(owner, plan, failedLog) }
        }

        println("\n✅ Migration apply finished.")
        println("  Failures logged to: ${failedLog.path}")
        println("\nNext: ./gradlew refreshRepoIndex   # refresh existing-repos.yaml")
    }

    private fun readResponse(): MigrateLeafResponse? {
        val file = File("ai-tasks/output/migrate-leaf-response.json")
        if (!file.exists()) {
            println("Error: response file not found: ${file.path}")
            println("Run without --apply first, then ask Claude to process the request.")
            return null
        }
        return try {
            json.decodeFromString<MigrateLeafResponse>(file.readText())
        } catch (e: Exception) {
            println("Error parsing response: ${e.message}")
            null
        }
    }

    private fun printPlan(plan: RepoPlan) {
        val triple = "${plan.target.topCategory}/${plan.target.subCategory}/${plan.target.leafCategory}"
        println("  ✏️  ${plan.name}  →  $triple")
        if (plan.topicsToAdd.isNotEmpty()) {
            println("       gh +: ${plan.topicsToAdd.joinToString(", ")}")
        }
        if (plan.topicsToRemove.isNotEmpty()) {
            println("       gh -: ${plan.topicsToRemove.joinToString(", ")}")
        }
        if (plan.moveFrom != null && plan.moveTo != null) {
            println("       fs:   ${plan.moveFrom.absolutePath}")
            println("          → ${plan.moveTo.absolutePath}")
        } else {
            println("       fs:   (no local clone, skip move)")
        }
    }

    private fun executePlan(
        owner: String,
        plan: RepoPlan,
        failedLog: File,
    ) {
        var anyFailure = false

        plan.topicsToAdd.forEach { topic ->
            if (!ghService.addTopic(owner, plan.name, topic)) {
                anyFailure = true
                failedLog.appendText("[${nowStamp()}] ${plan.name}: add-topic $topic FAILED\n")
            }
            Thread.sleep(AppConfig.TOPIC_API_DELAY_MS)
        }
        plan.topicsToRemove.forEach { topic ->
            if (!ghService.removeTopic(owner, plan.name, topic)) {
                anyFailure = true
                failedLog.appendText("[${nowStamp()}] ${plan.name}: remove-topic $topic FAILED\n")
            }
            Thread.sleep(AppConfig.TOPIC_API_DELAY_MS)
        }

        if (plan.moveFrom != null && plan.moveTo != null) {
            try {
                plan.moveTo.parentFile?.mkdirs()
                if (plan.moveTo.exists()) {
                    failedLog.appendText("[${nowStamp()}] ${plan.name}: target ${plan.moveTo} already exists, skipped move\n")
                    anyFailure = true
                } else {
                    Files.move(plan.moveFrom.toPath(), plan.moveTo.toPath(), StandardCopyOption.ATOMIC_MOVE)
                }
            } catch (e: Exception) {
                anyFailure = true
                failedLog.appendText("[${nowStamp()}] ${plan.name}: mv ${plan.moveFrom} → ${plan.moveTo} FAILED (${e.message})\n")
            }
        }

        println(if (anyFailure) "  ⚠ ${plan.name} (some steps failed; see migrate-failed.log)" else "  ✓ ${plan.name}")
    }

    private fun buildLocalCloneIndex(): Map<String, File> {
        val roots = workRoots()
        val out = mutableMapOf<String, File>()
        for (root in roots) {
            if (!root.isDirectory) continue
            walkForRepos(root, depth = 0, maxDepth = 6, out = out)
        }
        return out
    }

    private fun walkForRepos(
        dir: File,
        depth: Int,
        maxDepth: Int,
        out: MutableMap<String, File>,
    ) {
        if (depth > maxDepth) return
        val entries = dir.listFiles() ?: return
        if (entries.any { it.isDirectory && it.name == ".git" }) {
            out.putIfAbsent(dir.name, dir)
            return
        }
        for (entry in entries) {
            if (!entry.isDirectory) continue
            if (entry.name.startsWith(".")) continue
            walkForRepos(entry, depth + 1, maxDepth, out)
        }
    }

    private fun workRoots(): List<File> {
        val roots = mutableListOf<File>()
        val configured = File(AppConfig.defaultWorkDir)
        val booksDone = File(configured.parentFile, "books-done")
        if (booksDone.isDirectory) roots.add(booksDone)
        extraWorkDir?.let {
            val extra = File(it)
            if (extra.isDirectory) roots.add(extra)
        }
        return roots
    }

    private fun nowStamp(): String =
        java.time.OffsetDateTime
            .now()
            .format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}
