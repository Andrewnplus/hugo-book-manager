plugins {
    kotlin("jvm") version "2.4.10"
    kotlin("plugin.serialization") version "2.4.10"
    application
    id("com.diffplug.spotless") version "8.8.0"
}

group = "com.nplus"
version = "1.0.0"

repositories {
    mavenCentral()
}

val cliktVersion = "5.1.0"
val serializationVersion = "1.11.0"
val slf4jVersion = "2.0.18"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")

    implementation("com.github.ajalt.clikt:clikt:$cliktVersion")

    implementation("org.yaml:snakeyaml:2.6")

    implementation("org.slf4j:slf4j-simple:$slf4jVersion")

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

registerCliTask("refreshBookHealth", "Scan local book clones and refresh the portal's content-health artifact") {
    args = listOf("refresh-book-health")
}

registerCliTask(
    "refreshOverviewCoverage",
    "Audit every local book clone's deep overview and refresh the portal's coverage artifact",
) {
    args = listOf("refresh-overview-coverage")
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
        standardInput = System.`in`
    }
}
