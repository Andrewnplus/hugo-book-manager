plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
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

    // kotlin-csv for CSV reading
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.10.0")

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
    jvmToolchain(21)
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
    configure()
}

registerCliTask("checkEnv", "Check environment prerequisites (gh CLI, authentication, etc.)") {
    args = listOf("check-env")
}

registerCliTask("createRepos", "Create GitHub repos from CSV file") {
    doFirst {
        val csvFile =
            project.findProperty("csv")?.toString()
                ?: throw GradleException("Please specify CSV file: -Pcsv=data/test.csv")
        val dryRun = project.findProperty("dryRun")?.toString()?.toBoolean() ?: false
        val startFrom = project.findProperty("startFrom")?.toString()?.toInt() ?: 1

        val argList = mutableListOf("create-repos", "--csv", csvFile)
        if (dryRun) argList.add("--dry-run")
        if (startFrom > 1) {
            argList.add("--start-from")
            argList.add(startFrom.toString())
        }
        args = argList
    }
}

registerCliTask("updateRenovate", "Batch update renovate.json in multiple repos") {
    doFirst {
        val parentDir =
            project.findProperty("parentDir")?.toString()
                ?: throw GradleException("Please specify parent directory: -PparentDir=/path/to/books")
        val dryRun = project.findProperty("dryRun")?.toString()?.toBoolean() ?: false
        val noPush = project.findProperty("noPush")?.toString()?.toBoolean() ?: false
        val recursive = project.findProperty("recursive")?.toString()?.toBoolean() ?: false

        val argList = mutableListOf("update-renovate", "--parent-dir", parentDir)
        if (dryRun) argList.add("--dry-run")
        if (noPush) argList.add("--no-push")
        if (recursive) argList.add("--recursive")
        args = argList
    }
}

registerCliTask("initBook", "Initialize a new book repo from YAML input file") {
    doFirst {
        val inputFile = project.findProperty("input")?.toString() ?: "templates/book-input.yaml"
        val dryRun = project.findProperty("dryRun")?.toString()?.toBoolean() ?: false

        val argList = mutableListOf("init-book", "--input", inputFile)
        if (dryRun) argList.add("--dry-run")
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

registerCliTask("mergePrs", "Batch merge Renovate PRs with passing CI") {
    doFirst {
        val parentDir =
            project.findProperty("parentDir")?.toString()
                ?: throw GradleException("Please specify parent directory: -PparentDir=/path/to/books")
        val mergeMethod = project.findProperty("mergeMethod")?.toString() ?: "merge"

        val argList = mutableListOf("merge-prs", "--parent-dir", parentDir)
        argList.add("--merge-method")
        argList.add(mergeMethod)
        args = argList
    }
}

registerCliTask("generatePrompt", "Generate prompt templates for book project tasks") {
    doFirst {
        val type = project.findProperty("type")?.toString()
        val output = project.findProperty("output")?.toString()
        val list = project.findProperty("list")?.toString()?.toBoolean() ?: false

        val argList = mutableListOf("generate-prompt")
        if (list) {
            argList.add("--list")
        } else if (type != null) {
            argList.add("--type")
            argList.add(type)
            if (output != null) {
                argList.add("--output")
                argList.add(output)
            }
        }
        args = argList
    }
}

// Hugo documentation server tasks
tasks.register<Exec>("hugoServer") {
    group = "documentation"
    description = "Start Hugo development server for docs site"
    workingDir = file("docs")
    commandLine("hugo", "server", "--buildDrafts", "--buildFuture")
}

tasks.register<Exec>("hugoBuild") {
    group = "documentation"
    description = "Build Hugo docs site"
    workingDir = file("docs")
    commandLine("hugo", "--gc", "--minify")
}

// Distribution task to create a fat jar
tasks.register<Jar>("fatJar") {
    group = "build"
    description = "Creates a fat JAR with all dependencies"
    archiveClassifier.set("all")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "com.nplus.bookmanager.MainKt"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get())
}
