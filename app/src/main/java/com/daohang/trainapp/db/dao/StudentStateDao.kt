package com.daohang.trainapp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.daohang.trainapp.db.models.StudentStateModel

@Dao
interface StudentStateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudentLogin(model: StudentStateModel)

    @Query("delete from student_state where id=:id")
    fun delete(id: Int)

    @Query("select * from student_state where hasUploaded = 0 order by time")
    fun findStudentUnUploaded(): LiveData<List<StudentStateModel>>
}