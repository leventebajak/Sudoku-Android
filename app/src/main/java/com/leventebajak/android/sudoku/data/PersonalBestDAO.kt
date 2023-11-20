package com.leventebajak.android.sudoku.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.leventebajak.sudokugenerator.SudokuGenerator

@Dao
interface PersonalBestDAO {
    @Query("SELECT * FROM PersonalBest")
    fun getAll(): List<PersonalBest>

    @Query("SELECT * FROM PersonalBest WHERE difficulty = :difficulty")
    fun getByDifficulty(difficulty: SudokuGenerator.Difficulty): PersonalBest?

    @Query("SELECT COUNT(*) FROM PersonalBest")
    fun getCount(): Int

    @Insert
    fun insert(personalBest: PersonalBest): Long

    @Update
    fun update(personalBest: PersonalBest)

    @Delete
    fun delete(personalBest: PersonalBest)
}