package com.example.apptask

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.io.Serializable


class TaskListActivity : AppCompatActivity() {
    private var taskList = arrayListOf(
        Task(0,"Título 1", "Descrição 1"),
        Task(1,"Título 2", "Descrição 2"),
        Task(2,"Título 3", "Descrição 3"),
        Task(3,"Título 4", "Descrição 4")
    )
    private lateinit var conteiner : LinearLayout
    private val adapter: TaskListAdapter = TaskListAdapter(::openTaskDetailView)
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {

            val data = result.data
            val taskAction = data?.getSerializableExtra(TASK_ACTION_RESULT) as TaskAction
            val task: Task = taskAction.task
            val newList= arrayListOf<Task>()
                .apply {
                    addAll(taskList)
            }


            newList.remove(task)
            showMessage(conteiner,"Item ${task.title} deletado!")

            if(newList.size==0){
                conteiner.visibility=View.VISIBLE
            }
            adapter.submitList(newList)
            taskList=newList
        }
    }
    fun showMessage(view: View,message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        val taskListView: RecyclerView = findViewById(R.id.rv_task_list)
        taskListView.layoutManager = LinearLayoutManager(this)

        conteiner = findViewById(R.id.contentLinear)

        adapter.submitList(taskList)
        taskListView.adapter = adapter

        val fab= findViewById<FloatingActionButton>(R.id.fabadd)
        fab.setOnClickListener {
            showMessage(it, "Here's a Snackbar")
        }



        taskListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {

                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_task_detail, menu)
        return true
    }

    private fun openTaskDetailView(task: Task) {
        val intent = TaskDetailActivity.start(this, task)
        startForResult.launch(intent)

    }
}
sealed class ActionType : Serializable {
    object DELETE : ActionType()
    object UPDATE : ActionType()
    object CREATE : ActionType()
}
data class TaskAction(
    val task: Task,
    val actionType: ActionType
) : Serializable
const val TASK_ACTION_RESULT = "TASK_ACTION_RESULT"
