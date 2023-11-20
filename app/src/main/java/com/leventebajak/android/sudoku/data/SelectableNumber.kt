package com.leventebajak.android.sudoku.data

data class SelectableNumber(
    val value: Int,
    var remaining: Int,
    var selected: Boolean
)