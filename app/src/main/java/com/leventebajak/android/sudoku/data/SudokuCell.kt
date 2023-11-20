package com.leventebajak.android.sudoku.data

class SudokuCell(val data: SudokuCellData) {
    constructor(clue: Int, solution: Int, row: Int, column: Int, gameID: Long) : this(
        SudokuCellData(null, clue, clue == EMPTY, solution, row, column, gameID)
    ) {
        if (clue != EMPTY && solution != clue)
            throw IllegalArgumentException("Clue and solution do not match in cell ($row, $column)")
    }

    fun isSolved() = data.value == data.solution
    fun setValue(value: Int) {
        if (value !in 0..9)
            throw IllegalArgumentException("Value must be between 0 and 9")

        if (!data.editable)
            throw IllegalStateException("Cell is not editable")

        data.value = value
    }

    companion object {
        const val EMPTY = 0
    }
}