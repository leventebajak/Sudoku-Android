package com.leventebajak.sudokugenerator

/**
 * A 9x9 [Sudoku] board.
 *
 * @property cells The cells of the board.
 * @throws IllegalArgumentException If the board is not 9x9 or contains invalid values.
 * @see String.toBoard
 * @see Matrix.toBoard
 */
data class Board(private val cells: Array<IntArray> = Array(9) { IntArray(9) }) {
    init {
        if (cells.size != 9)
            throw IllegalArgumentException("Sudoku board must be 9x9")
        for (row in 0..8) {
            if (cells[row].size != 9)
                throw IllegalArgumentException("Sudoku board must be 9x9")
            for (col in 0..8) {
                if (cells[row][col] !in 0..9)
                    throw IllegalArgumentException("Cell value must be between 0 and 9")
            }
        }
    }

    /**
     * Gets the value of the cell at the given index.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return The value of the cell at the given [row] and [column][col].
     */
    operator fun get(row: Int, col: Int): Int = cells[row][col]

    /**
     * Sets the value of the cell at the given [row] and [column][col].
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @param value The value to set the cell to.
     * @throws IllegalArgumentException If the value is not between 0 and 9.
     */
    operator fun set(row: Int, col: Int, value: Int) {
        if (value !in 0..9)
            throw IllegalArgumentException("Cell value must be between 0 and 9")
        cells[row][col] = value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        return cells.contentDeepEquals(other.cells)
    }

    override fun hashCode(): Int {
        return cells.contentDeepHashCode()
    }
}

/**
 * Converts a multiline string to a sudoku [Board].
 *
 * @return A 9x9 array of numbers.
 */
fun String.toBoard(): Board {
    return Board(
        this.trimIndent().lines().map { line ->
            line.trim().map {
                it - '0'
            }.toIntArray()
        }.toTypedArray()
    )
}

/**
 * Converts the [Matrix] to a sudoku [Board].
 *
 * @return A 9x9 array of numbers.
 */
fun Matrix.toBoard(): Board {
    val board = Board()
    for (bitSet in this.rows) {
        val cellConstraint = bitSet.nextSetBit(0)
        if (cellConstraint == -1 || cellConstraint >= 81)
            throw IllegalArgumentException("com.leventebajak.sudoku.Matrix does not represent a Sudoku board")

        val row = cellConstraint / 9
        val col = cellConstraint % 9
        if (board[row, col] != 0)
            throw IllegalArgumentException("Multiple numbers in the same cell")

        val rowConstraint = bitSet.nextSetBit(81)
        if (rowConstraint == -1 || rowConstraint >= 162)
            throw IllegalArgumentException("com.leventebajak.sudoku.Matrix does not represent a Sudoku board")

        val number = rowConstraint - 81 - row * 9 + 1

        board[row, col] = number
    }
    return board
}