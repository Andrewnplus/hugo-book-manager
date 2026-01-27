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

    // HTML parsing
    implementation("org.jsoup:jsoup:1.18.3")

    // PDF parsing
    implementation("org.apache.pdfbox:pdfbox:3.0.3")

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

tasks.register<JavaExec>("checkEnv") {
    group = "book-manager"
    description = "Check environment prerequisites (gh CLI, authentication, etc.)"
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.nplus.bookmanager.MainKt")
    args = listOf("check-env")
}

tasks.register<JavaExec>("createRepos") {
    group = "book-manager"
    description = "Create GitHub repos from CSV file"
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.nplus.bookmanager.MainKt")

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

tasks.register<JavaExec>("updateRenovate") {
    group = "book-manager"
    description = "Batch update renovate.json in multiple repos"
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.nplus.bookmanager.MainKt")

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

tasks.register<JavaExec>("initBook") {
    group = "book-manager"
    description = "Initialize a new book repo from YAML input file"
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.nplus.bookmanager.MainKt")

    doFirst {
        val inputFile = project.findProperty("input")?.toString() ?: "templates/book-input.yaml"
        val dryRun = project.findProperty("dryRun")?.toString()?.toBoolean() ?: false

        val argList = mutableListOf("init-book", "--input", inputFile)
        if (dryRun) argList.add("--dry-run")
        args = argList
    }
}

tasks.register<JavaExec>("initBooks") {
    group = "book-manager"
    description = "Initialize multiple book repos from a queue file"
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.nplus.bookmanager.MainKt")

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

tasks.register<JavaExec>("rebuildDocs") {
    group = "book-manager"
    description = "Rebuild docs structure in an existing Hugo Book project"
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.nplus.bookmanager.MainKt")

    doFirst {
        val repoDir =
            project.findProperty("repoDir")?.toString()
                ?: throw GradleException("Please specify repo directory: -PrepoDir=/path/to/book")
        val toc = project.findProperty("toc")?.toString()
        val tocText = project.findProperty("tocText")?.toString()
        val dryRun = project.findProperty("dryRun")?.toString()?.toBoolean() ?: false
        val yes = project.findProperty("yes")?.toString()?.toBoolean() ?: false

        val argList = mutableListOf("rebuild-docs", "--repo-dir", repoDir)
        if (toc != null) {
            argList.add("--toc")
            argList.add(toc)
        }
        if (tocText != null) {
            argList.add("--toc-text")
            argList.add(tocText)
        }
        if (dryRun) argList.add("--dry-run")
        if (yes) argList.add("--yes")
        args = argList
    }
}

tasks.register<JavaExec>("mergePrs") {
    group = "book-manager"
    description = "Batch merge Renovate PRs with passing CI"
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.nplus.bookmanager.MainKt")

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

tasks.register<JavaExec>("cleanDocs") {
    group = "book-manager"
    description = "Clean HTML/MHTML documents and convert to Markdown"
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.nplus.bookmanager.MainKt")

    doFirst {
        val inputDir =
            project.findProperty("inputDir")?.toString()
                ?: throw GradleException("Please specify input directory: -PinputDir=/path/to/html/files")
        val outputDir = project.findProperty("outputDir")?.toString()
        val single = project.findProperty("single")?.toString()
        val dryRun = project.findProperty("dryRun")?.toString()?.toBoolean() ?: false

        val argList = mutableListOf("clean-docs", "--input-dir", inputDir)
        if (outputDir != null) {
            argList.add("--output-dir")
            argList.add(outputDir)
        }
        if (single != null) {
            argList.add("--single")
            argList.add(single)
        }
        if (dryRun) argList.add("--dry-run")
        args = argList
    }
}

tasks.register<JavaExec>("convertDocs") {
    group = "book-manager"
    description = "Convert cleaned Markdown to Hugo-book format using AI"
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.nplus.bookmanager.MainKt")

    doFirst {
        val inputDir =
            project.findProperty("inputDir")?.toString()
                ?: throw GradleException("Please specify input directory: -PinputDir=/path/to/md/files")
        val outputDir = project.findProperty("outputDir")?.toString()
        val single = project.findProperty("single")?.toString()
        val prompt = project.findProperty("prompt")?.toString()
        val delay = project.findProperty("delay")?.toString()
        val startFrom = project.findProperty("startFrom")?.toString()
        val dryRun = project.findProperty("dryRun")?.toString()?.toBoolean() ?: false

        val argList = mutableListOf("convert-docs", "--input-dir", inputDir)
        if (outputDir != null) {
            argList.add("--output-dir")
            argList.add(outputDir)
        }
        if (single != null) {
            argList.add("--single")
            argList.add(single)
        }
        if (prompt != null) {
            argList.add("--prompt")
            argList.add(prompt)
        }
        if (delay != null) {
            argList.add("--delay")
            argList.add(delay)
        }
        if (startFrom != null) {
            argList.add("--start-from")
            argList.add(startFrom)
        }
        if (dryRun) argList.add("--dry-run")
        args = argList
    }
}

tasks.register<JavaExec>("generatePrompt") {
    group = "book-manager"
    description = "Generate prompt templates for book project tasks"
    dependsOn("classes")
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.nplus.bookmanager.MainKt")

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
