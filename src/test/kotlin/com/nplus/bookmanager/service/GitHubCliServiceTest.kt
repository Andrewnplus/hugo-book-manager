package com.nplus.bookmanager.service

import kotlin.test.Test
import kotlin.test.assertEquals

class GitHubCliServiceTest {
    private fun build(
        owner: String = "nplus-father",
        repoName: String = "clean-code",
        description: String = "Clean Code reading notes",
        templateRepo: String = "Andrewnplus/hugo-book-template",
        isPrivate: Boolean = true,
    ) = GitHubCliService.buildCreateRepoCommand(owner, repoName, description, templateRepo, isPrivate)

    @Test
    fun `create command targets the owner explicitly, not the authenticated user`() {
        assertEquals(
            "gh repo create nplus-father/clean-code --template Andrewnplus/hugo-book-template " +
                "--private --description 'Clean Code reading notes'",
            build(),
        )
    }

    @Test
    fun `create command escapes single quotes so apostrophes in titles survive the shell`() {
        // '\'' closes the quoted run, emits a literal quote, reopens it —
        // /bin/sh hands gh the description back as one argument.
        assertEquals(
            "--description 'Andrew'\\''s Book'",
            build(description = "Andrew's Book").substringAfter("--private "),
        )
    }

    @Test
    fun `create command switches to --public when the repo is not private`() {
        assertEquals(
            "gh repo create nplus-father/clean-code --template Andrewnplus/hugo-book-template " +
                "--public --description 'Clean Code reading notes'",
            build(isPrivate = false),
        )
    }

    @Test
    fun `create command leaves shell metacharacters in the description literal`() {
        // Inside single quotes $ and ` are inert; the description must reach
        // gh unexpanded rather than being substituted by the shell.
        assertEquals(
            "--description '\$HOME and `whoami` stay literal'",
            build(description = "\$HOME and `whoami` stay literal").substringAfter("--private "),
        )
    }
}
