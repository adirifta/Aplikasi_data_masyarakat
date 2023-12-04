package com.example.project.data.dao

import android.icu.text.StringSearch
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.project.data.entity.User

@Dao
interface UserDao {
    @Query("SELECT * FROM user ORDER BY full_name ASC")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE full_name LIKE '%' || :search || '%'")
    fun searchByName(search: String): List<User>

    @Query("SELECT * FROM user WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM user WHERE id = :id")
    fun get(id: Int) : User

    @Query("SELECT * FROM user WHERE nik = :nik")
    fun getUserByNIK(nik: String): User?

    @Update
    fun update(user: User)
}