package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.service.GitHubCliService
import com.nplus.bookmanager.util.CliFormatter
import com.nplus.bookmanager.util.ProcessRunner

/**
 * Command to check environment prerequisites
 */
class CheckEnvCommand(
    private val ghService: GitHubCliService = GitHubCliService(),
) : CliktCommand(name = "check-env") {
    override fun help(context: com.github.ajalt.clikt.core.Context) = "Check environment prerequisites"

    override fun run() {
        CliFormatter.printHeader("Hugo Book Manager - Environment Check")
        println()

        var allOk = true

        // 1. Check GitHub CLI installation
        print("1. GitHub CLI (gh)... ")
        if (ghService.isGhInstalled()) {
            val version = ProcessRunner.executeForOutput("gh --version")?.lines()?.firstOrNull() ?: "unknown"
            println("OK ($version)")
        } else {
            println("NOT FOUND")
            println("   Install from: https://cli.github.com/")
            allOk = false
        }

        // 2. Check GitHub authentication
        print("2. GitHub authentication... ")
        if (ghService.isAuthenticated()) {
            val username = ghService.getUsername() ?: "unknown"
            println("OK (logged in as: $username)")
        } else {
            println("NOT AUTHENTICATED")
            println("   Run: gh auth login")
            allOk = false
        }

        // 3. Check Git installation
        print("3. Git... ")
        val gitVersion = ProcessRunner.executeForOutput("git --version")
        if (gitVersion != null) {
            println("OK ($gitVersion)")
        } else {
            println("NOT FOUND")
            allOk = false
        }

        // 4. Check local.properties
        print("4. local.properties... ")
        val localPropsFile = java.io.File("local.properties")
        if (localPropsFile.exists()) {
            println("OK")
        } else {
            println("NOT FOUND")
            println("   Copy .env.example to local.properties and configure")
            allOk = false
        }

        // 5. Check default work directory
        print("5. Default work directory... ")
        val workDir = java.io.File(AppConfig.defaultWorkDir)
        if (workDir.exists() && workDir.isDirectory) {
            println("OK (${AppConfig.defaultWorkDir})")
        } else if (AppConfig.defaultWorkDir == System.getProperty("user.home")) {
            println("OK (using home directory)")
        } else {
            println("NOT FOUND (${AppConfig.defaultWorkDir})")
            println("   Will be created when needed")
        }

        // 6. Check ImageMagick — optional, only shrinks the downloaded cover
        print("6. ImageMagick (convert)... ")
        val convertVersion = ProcessRunner.executeForOutput("convert --version")?.lines()?.firstOrNull()
        if (convertVersion != null) {
            println("OK ($convertVersion)")
        } else {
            println("NOT FOUND (optional)")
            println("   Covers will be ~3x larger without it: apt install imagemagick")
        }

        // Summary
        println()
        val message = if (allOk) "All checks passed! Ready to use." else "Some checks failed. Please fix the issues above."
        CliFormatter.printSectionHeader(message)

        // Print configuration summary
        println()
        println("Current Configuration:")
        println(AppConfig.toString())
    }
}
