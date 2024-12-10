package com.example.teamtasker.task

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtasker.R
import com.example.teamtasker.chat.ChatActivity
import com.example.teamtasker.data.Task
import com.example.teamtasker.data.TaskAdapter
import com.example.teamtasker.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TaskActivity : AppCompatActivity() {

    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()
    private val db = FirebaseFirestore.getInstance()
    private val currentUserId: String? by lazy { FirebaseAuth.getInstance().currentUser?.uid }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        if (currentUserId == null) {
            Toast.makeText(this, "Usuário não autenticado. Faça login novamente.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setupUI()
        loadTasksFromFirebase()
    }

    private fun setupUI() {
        val tasksRecyclerView: RecyclerView = findViewById(R.id.tasksRecyclerView)
        tasksRecyclerView.layoutManager = LinearLayoutManager(this)

        taskAdapter = TaskAdapter(taskList) { task -> updateTaskStatusInFirebase(task) }
        tasksRecyclerView.adapter = taskAdapter

        findViewById<Button>(R.id.addTaskButton).setOnClickListener {
            val titleInput = findViewById<EditText>(R.id.taskInput)
            val descriptionInput = findViewById<EditText>(R.id.taskDescriptionInput)

            val title = titleInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                val newTask = Task("", title, description, false)
                addTaskToFirebase(newTask)

                titleInput.text.clear()
                descriptionInput.text.clear()
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }



        findViewById<ImageView>(R.id.messageButtonTask).setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        findViewById<ImageView>(R.id.logoutButton)?.setOnClickListener {
            logoutUser()
        }

        window.statusBarColor = getColor(R.color.blueDark)
    }

    private fun addTaskToFirebase(newTask: Task) {
        val userId = currentUserId
        if (userId == null) {
            Toast.makeText(this, "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        newTask.userId = userId
        val taskRef = db.collection("tasks").document() // Cria um novo documento
        newTask.id = taskRef.id // Atribui o id gerado pelo Firestore ao objeto Task

        taskRef.set(newTask)
            .addOnSuccessListener {
                taskList.add(newTask) // Adiciona a tarefa à lista
                taskAdapter.notifyItemInserted(taskList.size - 1) // Notifica o adaptador
                Toast.makeText(this, "Tarefa adicionada!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar tarefa: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun updateTaskStatusInFirebase(task: Task) {
        val taskRef = db.collection("tasks").document(task.id)
        taskRef.update("isCompleted", task.isCompleted)
            .addOnSuccessListener {
                // Atualização bem-sucedida
                taskAdapter.notifyDataSetChanged() // Atualiza o adaptador para refletir a mudança
                Toast.makeText(this, "Status atualizado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Caso ocorra erro
                Toast.makeText(this, "Erro ao atualizar status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadTasksFromFirebase() {
        val userId = currentUserId
        if (userId == null) {
            Toast.makeText(this, "Erro: Usuário não autenticado.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("tasks")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                taskList.clear()

                for (document in result) {
                    val task = document.toObject(Task::class.java)
                    task.id = document.id
                    taskList.add(task)
                }

                taskAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar tarefas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        taskList.clear()
        taskAdapter.notifyDataSetChanged()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
