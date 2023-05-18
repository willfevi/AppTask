package com.example.apptask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TaskListAdapter(
    private val openTaskDetailView:(task:Task) -> Unit
) :ListAdapter<Task,TasklistViewHolder>(TaskListAdapter) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasklistViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TasklistViewHolder(view)
    }
   companion object : DiffUtil.ItemCallback<Task>(){
       override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
           return oldItem ==newItem
       }

       override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
         return oldItem.title==newItem.title && oldItem.description==newItem.description
       }

   }



    override fun onBindViewHolder(holder: TasklistViewHolder, position: Int) {
        val task =getItem(position)
        holder.bind(task, openTaskDetailView)
    }
}

class TasklistViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private val TaskTitulo = view.findViewById<TextView>(R.id.text_view_title)
    private val TaskDescricao = view.findViewById<TextView>(R.id.text_view_description)

    fun bind(task: Task,openTaskDetailView:(task:Task)->Unit) {
        TaskTitulo.text = task.title
        TaskDescricao.text = task.description

        view.setOnClickListener{
            openTaskDetailView.invoke(task)}
    }
}