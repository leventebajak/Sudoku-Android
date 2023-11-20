package com.leventebajak.android.sudoku.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.leventebajak.sudokugenerator.SudokuGenerator

@Entity
data class GameInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    val difficulty: SudokuGenerator.Difficulty,
    var seconds: Int
)