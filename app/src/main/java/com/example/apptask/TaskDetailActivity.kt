package com.example.apptask

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class TaskDetailActivity : AppCompatActivity() {

    private var task: Task?= null
    private lateinit var btnTask:Button

    companion object {
        const val TASK_EXTRA = "task_detail_view"

        fun start(context: Context, task: Task?): Intent {
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

        task = intent.getSerializableExtra(TASK_EXTRA) as Task?

        val edtTitle = findViewById<EditText>(R.id.edit_text_title)
        val edtDescription = findViewById<EditText>(R.id.edit_text_descricption)
        btnTask = findViewById<Button>(R.id.btn_task)

        if (task!= null){
            edtTitle.setText(task!!.title)
            edtDescription .setText(task!!.description)
        }


        btnTask.setOnClickListener{
            val title = edtTitle.text.toString()
            val desciption=edtDescription.text.toString()

            if (title.isNotEmpty()&&desciption.isNotEmpty()){
                if (task==null){
                    addOrUpdateTask(0,title,desciption,ActionType.CREATE)
                }else {
                    addOrUpdateTask(task!!.id,title,desciption,ActionType.UPDATE)
                }
            }else{
                showMessage(it,"Complete os campos!")
            }

        }




       // tv_Title = findViewById(R.id.tv_task_detail)
        //tv_Title.text = task?.title?:"Adicione uma tarefa!"
    }

    private fun addOrUpdateTask(id:Int,title:String,desciption:String,actionType:ActionType){
        val task = Task(id,title,desciption)
        returnAction(task, actionType)

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_task_detail, menu)
        return true
    }
    private fun returnAction(task: Task, actionType: ActionType) {
        val intent = Intent().apply {
            val taskAction = TaskAction(task, actionType)
            putExtra(TASK_ACTION_RESULT, taskAction)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_task -> {
                if (task != null) {
                    returnAction(task!!, ActionType.DELETE)
                } else {
                    showMessage(btnTask, "Nenhum item para deletar!")
                }
                true
            }
            android.R.id.home -> { // Tratamento do botão "Voltar" ou "Cancelar"
                setResult(Activity.RESULT_CANCELED)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }




    private fun showMessage(view: View, message: String){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }
}
