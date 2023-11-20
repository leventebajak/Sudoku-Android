package com.leventebajak.android.sudoku.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [
        SudokuCellData::class,
        GameInfo::class,
        PersonalBest::class
    ],
    version = 1
)
@TypeConverters(DifficultyConverter::class)
abstract class SudokuDatabase : RoomDatabase() {
    abstract fun sudokuCellDAO(): SudokuCellDataDAO
    abstract fun gameInfoDAO(): GameInfoDAO
    abstract fun personalBestDAO(): PersonalBestDAO

    companion object {
        fun getDatabase(applicationContext: Context): SudokuDatabase {
            return Room.databaseBuilder(
                applicationContext,
                SudokuDatabase::class.java,
                "sudoku_database"
            ).fallbackToDestructiveMigration().build()
        }
    }
}