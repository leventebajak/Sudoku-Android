package com.leventebajak.android.sudoku.data

import androidx.room.TypeConverter
import com.leventebajak.sudokugenerator.SudokuGenerator

class DifficultyConverter {
    @TypeConverter
    fun toDifficulty(ordinal: Int): SudokuGenerator.Difficulty? {
        var ret: SudokuGenerator.Difficulty? = null
        for (diff in SudokuGenerator.Difficulty.values()) {
            if (diff.ordinal == ordinal) {
                ret = diff
                break
            }
        }
        return ret
    }

    @TypeConverter
    fun fromDifficulty(difficulty: SudokuGenerator.Difficulty): Int {
        return difficulty.ordinal
    }
}