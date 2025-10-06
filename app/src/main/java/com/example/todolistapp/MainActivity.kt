package com.example.todolistapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

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
        val jsonArray = JSONArray()
        for (task in taskList) {
            val obj = JSONObject()
            obj.put("name", task.name)
            obj.put("isDone", task.isDone)
            jsonArray.put(obj)
        }
        editor.putString("tasks_json", jsonArray.toString())
        editor.apply()
    }

    // Load tasks from SharedPreferences
    private fun loadTasks(): MutableList<Task> {
        val sharedPref = getSharedPreferences("tasks_pref", MODE_PRIVATE)
        val jsonString = sharedPref.getString("tasks_json", null)
        if (!jsonString.isNullOrEmpty()) {
            return try {
                val array = JSONArray(jsonString)
                val list = mutableListOf<Task>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val name = obj.optString("name", "")
                    val isDone = obj.optBoolean("isDone", false)
                    list.add(Task(name, isDone))
                }
                list
            } catch (e: Exception) {
                mutableListOf()
            }
        }
        // Backward compatibility with old storage (set of strings)
        val taskStrings = sharedPref.getStringSet("task_set", emptySet()) ?: emptySet()
        return taskStrings.map { Task.fromString(it) }.toMutableList()
    }

    // Update total and completed task counts
    private fun updateTaskCounters() {
        val total = taskList.size
        val completed = taskList.count { it.isDone }
        val notDone = total - completed
        textCompletedCount.text = "Completed: $completed"
        textTaskCount.text = "Not Done: $notDone"
    }
}
