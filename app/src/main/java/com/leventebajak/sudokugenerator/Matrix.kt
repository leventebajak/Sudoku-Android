package com.leventebajak.sudokugenerator

import java.util.BitSet

/**
 * A matrix of binary values.
 *
 * @property columns The number of columns in the matrix.
 * @property rows The rows of the matrix as a list of [BitSet]s.
 */
class Matrix
/**
 * Creates a [Matrix] with the given number of [columns] and a list of [BitSet]s.
 *
 * @param columns The number of columns in the matrix.
 * @param rows The rows of the matrix.
 */(columns: Int, rows: List<BitSet>) {
    val rows: MutableList<BitSet>
    var columns: Int = columns
        private set

    init {
        this.rows = rows.toMutableList()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix

        if (columns != other.columns) return false
        if (rows != other.rows) return false

        return true
    }

    override fun hashCode(): Int {
        var result = columns
        result = 31 * result + rows.hashCode()
        return result
    }
}