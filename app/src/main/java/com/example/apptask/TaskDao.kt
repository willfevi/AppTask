package com.example.apptask

import androidx.room.*

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task)

    @Query("SELECT * FROM task")
    fun getAll(): List<Task>

    @Delete
    fun delete(task: Task)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(task: Task)

    @Query ("DELETE FROM TASK")
    fun deleteAll()
}
