package com.leventebajak.android.sudoku.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SudokuCellDataDAO {
    @Query("SELECT * FROM SudokuCellData")
    fun getAll(): List<SudokuCellData>

    @Query("SELECT * FROM SudokuCellData WHERE gameId = :gameId")
    fun getAllByGameID(gameId: Long): List<SudokuCellData>

    @Query("SELECT COUNT(*) FROM SudokuCellData")
    fun getCount(): Int

    @Insert
    fun insert(sudokuCellData: SudokuCellData): Long

    @Update
    fun update(sudokuCellData: SudokuCellData)

    @Delete
    fun delete(sudokuCellData: SudokuCellData)
}