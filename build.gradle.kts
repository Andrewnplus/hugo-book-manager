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
    configure()
}

registerCliTask("checkEnv", "Check environment prerequisites (gh CLI, authentication, etc.)") {
    args = listOf("check-env")
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

