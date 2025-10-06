package com.example.todolistapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat

class TaskAdapter(
    private val context: Context,
    private val tasks: MutableList<Task>,
    private val updateCounters: () -> Unit,
    private val saveTasks: () -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = tasks.size
    override fun getItem(position: Int): Any = tasks[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val task = tasks[position]
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_task, parent, false)

        val taskName = view.findViewById<TextView>(R.id.textTaskName)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBoxTask)

        taskName.text = task.name

        // Remove previous listener before updating state to avoid spurious callbacks
        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = task.isDone
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            task.isDone = isChecked
            updateCounters()
            saveTasks()
        }

        return view
    }
}
