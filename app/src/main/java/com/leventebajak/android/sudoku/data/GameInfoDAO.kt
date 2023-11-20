package com.leventebajak.android.sudoku.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GameInfoDAO {
    @Query("SELECT * FROM GameInfo")
    fun getAll(): List<GameInfo>

    @Query("SELECT * FROM GameInfo WHERE id = :id")
    fun getByID(id: Long): GameInfo?

    @Query("SELECT * FROM GameInfo ORDER BY id DESC LIMIT 1")
    fun getLast(): GameInfo?

    @Query("SELECT COUNT(*) FROM GameInfo")
    fun getCount(): Int

    @Insert
    fun insert(gameInfo: GameInfo): Long

    @Update
    fun update(gameInfo: GameInfo)

    @Delete
    fun delete(gameInfo: GameInfo)
}