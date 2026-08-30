package com.nplus.bookmanager.util

object UserInput {
    fun confirm(prompt: String): Boolean {
        print("$prompt (yes/no): ")
        return readlnOrNull()?.lowercase() == "yes"
    }
}
