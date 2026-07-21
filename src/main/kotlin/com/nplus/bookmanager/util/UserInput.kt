package com.nplus.bookmanager.util

object UserInput {
    /** 顯示 prompt，讀取 yes/no。回傳 true 僅當使用者輸入 "yes" */
    fun confirm(prompt: String): Boolean {
        print("$prompt (yes/no): ")
        return readlnOrNull()?.lowercase() == "yes"
    }
}
