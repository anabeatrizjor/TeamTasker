package com.example.teamtasker.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtasker.R

class TaskAdapter(
    private val taskList: MutableList<Task>,
    private val onTaskStatusChanged: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.bind(task)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskTitleTextView: TextView = itemView.findViewById(R.id.taskTitleTextView)
        private val taskDescriptionTextView: TextView = itemView.findViewById(R.id.taskDescriptionTextView)
        private val taskStatusTextView: TextView = itemView.findViewById(R.id.taskStatusTextView)
        private val taskCompleteCheckBox: CheckBox = itemView.findViewById(R.id.taskCompleteCheckBox)

        fun bind(task: Task) {
            taskTitleTextView.text = task.title
            taskDescriptionTextView.text = task.description
            taskStatusTextView.text = if (task.isCompleted) "Concluída" else "Pendente"
            taskCompleteCheckBox.isChecked = task.isCompleted

            // Atualiza o status da tarefa no Firebase quando o CheckBox for alterado
            taskCompleteCheckBox.setOnCheckedChangeListener { _, isChecked ->
                task.isCompleted = isChecked
                onTaskStatusChanged(task) // Chama o método para atualizar o status no Firestore
            }
        }
    }
}

