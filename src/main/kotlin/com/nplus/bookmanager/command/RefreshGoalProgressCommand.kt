package com.nplus.bookmanager.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.nplus.bookmanager.config.AppConfig
import com.nplus.bookmanager.service.GoalProgressService
import java.io.File

class RefreshGoalProgressCommand : CliktCommand(name = "refresh-goal-progress") {
    override fun help(context: Context) = "Scan local clones and refresh the portal's derived goal progress artifact"

    override fun run() {
        val portalDir = File(AppConfig.portalDir)
        val notesDir = File(AppConfig.notesDir)
        if (!portalDir.isDirectory) {
            println("Error: PORTAL_DIR not found: ${portalDir.path} (set it in local.properties)")
            return
        }
        if (!notesDir.isDirectory) {
            println("Error: NOTES_DIR not found: ${notesDir.path} (set it in local.properties)")
            return
        }

        val booksDir = File(AppConfig.booksDir).takeIf { it.isDirectory }
        val service = GoalProgressService(portalDir = portalDir, notesDir = notesDir, booksDir = booksDir)
        val goals = service.loadGoals()
        println("Loaded ${goals.size} goal(s) from ${service.goalsFile.path}")

        val (progress, recent) = service.scan(goals)
        service.save(progress, recent)

        for (p in progress) {
            println("  ${p.goalId}: ${p.done}/${p.total} ${p.unit}")
        }
        println("✅ Saved ${progress.size} goal(s), ${recent.size} recent item(s) to ${service.progressFile.path}")
        println("→ Remember to commit + push the portal repo to publish the dashboard.")
    }
}
