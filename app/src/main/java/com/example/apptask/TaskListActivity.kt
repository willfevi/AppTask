package com.example.apptask

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

class TaskListActivity : AppCompatActivity() {

    private lateinit var container: LinearLayout
    private val adapter: TaskListAdapter = TaskListAdapter(::onListItemClicked)
    private lateinit var dataBase: AppDatabase
    private lateinit var dao: TaskDao

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val taskAction = data?.getSerializableExtra(TASK_ACTION_RESULT) as TaskAction?
                val task: Task = taskAction?.task ?: return@registerForActivityResult

                when (taskAction.actionType) {
                    ActionType.DELETE -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            deleteFromDatabase(task)
                        }
                        showMessage(container, "Item ${task.title} deletado!")
                    }
                    ActionType.CREATE -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            insertIntoDatabase(task)
                        }
                        showMessage(container, "Item ${task.title} adicionado!")
                    }
                    ActionType.UPDATE -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            updateDatabase(task)
                        }
                        showMessage(container, "Item ${task.title} atualizado!")
                    }
                }
            }
        }

    private fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        val taskListView: RecyclerView = findViewById(R.id.rv_task_list)
        taskListView.layoutManager = LinearLayoutManager(this)

        container = findViewById(R.id.contentLinear)

        taskListView.adapter = adapter

        val fab = findViewById<FloatingActionButton>(R.id.fabadd)
        fab.setOnClickListener {
            openTaskListDetail(null)
        }

        taskListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                    // Handle scroll to bottom
                }
            }
        })

        dataBase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "task_database"
        ).build()
        dao = dataBase.taskDao()

        CoroutineScope(Dispatchers.IO).launch {
            listFromDatabase()
        }
    }

    private suspend fun insertIntoDatabase(task: Task) {
        withContext(Dispatchers.IO) {
            dao.insert(task)
        }
        withContext(Dispatchers.Main) {
            listFromDatabase()
        }
    }

    private suspend fun deleteFromDatabase(task: Task) {
        withContext(Dispatchers.IO) {
            dao.delete(task)
        }
        withContext(Dispatchers.Main) {
            listFromDatabase()
        }
    }

    private suspend fun updateDatabase(task: Task) {
        withContext(Dispatchers.IO) {
            dao.update(task)
        }
        withContext(Dispatchers.Main) {
            listFromDatabase()
        }
    }
    private suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            dao.deleteAll()
        }
        withContext(Dispatchers.Main) {
            listFromDatabase()
        }
    }
    private suspend fun listFromDatabase() {
        val myDatabaseList: List<Task> = withContext(Dispatchers.IO) {
            dao.getAll()
        }
        withContext(Dispatchers.Main) {
            adapter.submitList(myDatabaseList)
            container.visibility = if (myDatabaseList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_task_list_activity, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_task -> {
                CoroutineScope(Dispatchers.IO).launch {
                    deleteAll()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun onListItemClicked(task: Task) {
        openTaskListDetail(task)
    }

    private fun openTaskListDetail(task: Task? = null) {
        val intent = TaskDetailActivity.start(this, task)
        startForResult.launch(intent)
    }
}

enum class ActionType {
    DELETE,
    UPDATE,
    CREATE
}

data class TaskAction(
    val task: Task,
    val actionType: ActionType
) : Serializable

const val TASK_ACTION_RESULT = "TASK_ACTION_RESULT"
