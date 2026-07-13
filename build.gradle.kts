plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    application
    id("com.diffplug.spotless") version "7.0.2"
}

group = "com.nplus"
version = "1.0.0"

repositories {
    mavenCentral()
}

val cliktVersion = "5.0.3"
val serializationVersion = "1.8.0"
val slf4jVersion = "2.0.17"

dependencies {
    // kotlinx-serialization for JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")

    // Clikt for CLI
    implementation("com.github.ajalt.clikt:clikt:$cliktVersion")

    // YAML parsing
    implementation("org.yaml:snakeyaml:2.3")

    // SLF4J Simple implementation (to suppress SLF4J warnings)
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")

    // Testing
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(25)
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}

application {
    mainClass.set("com.nplus.bookmanager.MainKt")
}

// Custom Gradle Tasks

fun registerCliTask(
    name: String,
    desc: String,
    configure: JavaExec.() -> Unit = {},
) = tasks.register<JavaExec>(name) {
    group = "book-manager"
    description = desc
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.nplus.bookmanager.MainKt")
    // CLI uses relative paths like `templates/existing-repos.yaml` and
    // `ai-tasks/`; pin CWD to the project dir so they resolve regardless of
    // where gradle was invoked from.
    workingDir = projectDir
    configure()
}

registerCliTask("checkEnv", "Check environment prerequisites (gh CLI, authentication, etc.)") {
    args = listOf("check-env")
}

registerCliTask("refreshRepoIndex", "Refresh the cached index of book repos for the configured owner") {
    args = listOf("refresh-repo-index")
}

registerCliTask("refreshGoalProgress", "Scan local clones and refresh the portal's derived goal progress artifact") {
    args = listOf("refresh-goal-progress")
}

registerCliTask("markRead", "Mark a book chapter as read (frontmatter read/readAt); omit chapter to list") {
    doFirst {
        val repo = project.findProperty("repo")?.toString() ?: error("usage: ./gradlew markRead -Prepo=<repo> [-Pchapter=<dir>]")
        val chapter = project.findProperty("chapter")?.toString()
        val argList = mutableListOf("mark-read", "--repo", repo)
        if (chapter != null) {
            argList.add("--chapter")
            argList.add(chapter)
        }
        args = argList
    }
}

registerCliTask("initBooks", "Initialize multiple book repos from a queue file") {
    doFirst {
        val queueFile = project.findProperty("queue")?.toString() ?: "templates/books-queue.yaml"
        val bookId = project.findProperty("id")?.toString()
        val status = project.findProperty("status")?.toString()?.toBoolean() ?: false
        val reset = project.findProperty("reset")?.toString()?.toBoolean() ?: false
        val dryRun = project.findProperty("dryRun")?.toString()?.toBoolean() ?: false

        val argList = mutableListOf("init-books", "--queue", queueFile)
        if (bookId != null) {
            argList.add("--id")
            argList.add(bookId)
        }
        if (status) argList.add("--status")
        if (reset) argList.add("--reset")
        if (dryRun) argList.add("--dry-run")
        args = argList
    }
}

registerCliTask("migrateTopicTiers", "Migrate book repos from 2-tier (top/sub) to 3-tier (top/sub/leaf) topics + folders") {
    doFirst {
        val apply = project.findProperty("apply")?.toString()?.toBoolean() ?: false
        val batch = project.findProperty("batch")?.toString()
        val limit = project.findProperty("limit")?.toString()
        // NOTE: do not use `name` here — `Project.name` shadows `-Pname=...`.
        val repoName = project.findProperty("repoName")?.toString()
        val extraWorkDir = project.findProperty("extraWorkDir")?.toString()

        val argList = mutableListOf("migrate-topic-tiers")
        if (apply) argList.add("--apply")
        if (batch != null) {
            argList.add("--batch")
            argList.add(batch)
        }
        if (limit != null) {
            argList.add("--limit")
            argList.add(limit)
        }
        if (repoName != null) {
            argList.add("--name")
            argList.add(repoName)
        }
        if (extraWorkDir != null) {
            argList.add("--extra-work-dir")
            argList.add(extraWorkDir)
        }
        args = argList
        // Apply phase prompts for batch confirmation; needs interactive stdin.
        standardInput = System.`in`
    }
}
