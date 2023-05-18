package com.example.apptask

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import java.io.Serializable

class TaskDetailActivity : AppCompatActivity() {
    private lateinit var task: Task

    companion object {
        const val TASK_EXTRA = "task_detail_view"

        fun start(context: Context, task: Task): Intent {
            val intent = Intent(context, TaskDetailActivity::class.java)
                .apply {
                    putExtra(TASK_EXTRA, task)
                }
            return intent
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        task = intent.getSerializableExtra(TASK_EXTRA) as Task


        val tv_Title = findViewById<TextView>(R.id.tv_task_detail)
        tv_Title.text = task.title
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_task_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_task -> {
                val actionType = ActionType.DELETE
                val taskAction = TaskAction(task, actionType)
                Intent().apply {
                    putExtra(TASK_ACTION_RESULT, taskAction)
                    setResult(Activity.RESULT_OK, this)
                }
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
