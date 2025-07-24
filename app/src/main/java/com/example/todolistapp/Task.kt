package com.example.todolistapp

data class Task(
    var name: String,
    var isDone: Boolean = false
) {
    override fun toString(): String {
        return "$name|$isDone"
    }

    companion object {
        fun fromString(data: String): Task {
            val parts = data.split("|")
            val name = parts.getOrNull(0) ?: ""
            val isDone = parts.getOrNull(1)?.toBoolean() ?: false
            return Task(name, isDone)
        }
    }
}
