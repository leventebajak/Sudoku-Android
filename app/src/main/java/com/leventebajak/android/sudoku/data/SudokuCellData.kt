package com.leventebajak.android.sudoku.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = GameInfo::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("gameId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class SudokuCellData(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var value: Int,
    val editable: Boolean,
    val solution: Int,
    val row: Int,
    val column: Int,
    var gameId: Long? = null
)
