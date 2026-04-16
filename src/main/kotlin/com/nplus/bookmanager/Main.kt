package com.nplus.bookmanager

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.nplus.bookmanager.command.CheckEnvCommand
import com.nplus.bookmanager.command.GeneratePromptCommand
import com.nplus.bookmanager.command.InitBooksCommand

/**
 * Main CLI application using Clikt
 */
class BookManagerCli : CliktCommand(name = "book-manager") {
    override fun help(context: com.github.ajalt.clikt.core.Context) =
        """
        Hugo Book Manager - Manage GitHub book repositories

        A tool for creating and managing Hugo-based book note repositories on GitHub.
        """.trimIndent()

    override fun run() = Unit
}

fun main(args: Array<String>) {
    BookManagerCli()
        .subcommands(
            CheckEnvCommand(),
            InitBooksCommand(),
            GeneratePromptCommand(),
        ).main(args)
}
