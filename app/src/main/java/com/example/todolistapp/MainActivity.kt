package com.example.todolistapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var taskList: MutableList<Task>
    private lateinit var adapter: TaskAdapter

    private lateinit var editTextTask: EditText
    private lateinit var buttonAdd: Button
    private lateinit var listViewTasks: ListView
    private lateinit var textTaskCount: TextView
    private lateinit var textCompletedCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        editTextTask = findViewById(R.id.editTextTask)
        buttonAdd = findViewById(R.id.buttonAdd)
        listViewTasks = findViewById(R.id.listViewTasks)
        textTaskCount = findViewById(R.id.textTaskCount)
        textCompletedCount = findViewById(R.id.textCompletedCount)

        // Load saved tasks
        taskList = loadTasks()

        // Set adapter
        adapter = TaskAdapter(this, taskList,
            updateCounters = { updateTaskCounters() },
            saveTasks = { saveTasks() }
        )
        listViewTasks.adapter = adapter

        // Add button logic
        buttonAdd.setOnClickListener {
            val taskText = editTextTask.text.toString().trim()
            if (taskText.isNotEmpty()) {
                taskList.add(Task(taskText))
                adapter.notifyDataSetChanged()
                editTextTask.text.clear()
                updateTaskCounters()
                saveTasks()
            } else {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
            }
        }

        updateTaskCounters()
    }

    // Save tasks to SharedPreferences
    private fun saveTasks() {
        val sharedPref = getSharedPreferences("tasks_pref", MODE_PRIVATE)
        val editor = sharedPref.edit()
        val taskStrings = taskList.map { it.toString() }.toSet()
        editor.putStringSet("task_set", taskStrings)
        editor.apply()
    }

    // Load tasks from SharedPreferences
    private fun loadTasks(): MutableList<Task> {
        val sharedPref = getSharedPreferences("tasks_pref", MODE_PRIVATE)
        val taskStrings = sharedPref.getStringSet("task_set", emptySet()) ?: emptySet()
        return taskStrings.map { Task.fromString(it) }.toMutableList()
    }

    // Update total and completed task counts
    private fun updateTaskCounters() {
        val total = taskList.size
        val completed = taskList.count { it.isDone }
        textTaskCount.text = "Total Tasks: $total"
        textCompletedCount.text = "Completed: $completed"
    }
}
