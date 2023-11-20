package com.leventebajak.android.sudoku.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.leventebajak.android.sudoku.R
import com.leventebajak.android.sudoku.data.SudokuCell
import com.leventebajak.android.sudoku.databinding.SudokuCellBinding
import java.lang.IllegalArgumentException
import kotlin.concurrent.thread


class SudokuBoardAdapter(
    private val listener: CellClickListener,
    cells: List<SudokuCell>,
    private val initialSelectedNumber: Int,
    private val highlightingEnabled: Boolean
) : RecyclerView.Adapter<SudokuBoardAdapter.SudokuViewHolder>() {

    private val items = mutableListOf<SudokuCell>()

    private var cellSize: Int = 0

    init {
        if (cells.size != 81)
            throw IllegalArgumentException("Sudoku board must have 81 cells but has ${cells.size}")

        items.addAll(cells)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SudokuViewHolder {
        cellSize = parent.width / 9
        return SudokuViewHolder(
            SudokuCellBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SudokuViewHolder, position: Int) {
        val sudokuCell = items[position]

        holder.binding.value.text = numberToText(sudokuCell.data.value)
        holder.binding.value.isEnabled = sudokuCell.data.editable

        if (highlightingEnabled)
            holder.binding.background.visibility =
                if (initialSelectedNumber != SudokuCell.EMPTY &&
                    initialSelectedNumber == sudokuCell.data.value
                ) View.VISIBLE
                else View.INVISIBLE

        holder.binding.root.layoutParams.width = cellSize
        holder.binding.root.layoutParams.height = cellSize

        val growShrinkAnim = AnimationUtils.loadAnimation(
            holder.binding.value.context,
            R.anim.grow_shrink
        )
        val shrinkAnim = AnimationUtils.loadAnimation(
            holder.binding.value.context,
            R.anim.shrink
        )

        if (holder.binding.value.isEnabled)
            holder.binding.value.setOnClickListener {
                val selectedNumber = listener.getSelectedNumber()

                val previousValue = sudokuCell.data.value

                if (selectedNumber == 0 || selectedNumber == sudokuCell.data.value) {
                    sudokuCell.setValue(0)
                    thread {
                        holder.binding.value.startAnimation(shrinkAnim)
                        Thread.sleep(shrinkAnim.duration)
                        holder.binding.value.text = ""
                    }
                    if (highlightingEnabled)
                        fadeOutBackground(holder)
                } else {
                    sudokuCell.setValue(selectedNumber)
                    holder.binding.value.startAnimation(growShrinkAnim)
                    holder.binding.value.text = sudokuCell.data.value.toString()
                    if (highlightingEnabled)
                        fadeInBackground(holder)
                }
                listener.onCellValueChanged(sudokuCell, previousValue)

            }
    }

    override fun getItemCount() = items.size

    fun isSolved() = items.all { it.isSolved() }

    interface CellClickListener {
        fun onCellValueChanged(item: SudokuCell, previousValue: Int)
        fun getSelectedNumber(): Int
    }

    inner class SudokuViewHolder(val binding: SudokuCellBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        fun fadeInBackground(holder: SudokuViewHolder) {
            val fadeIn = AnimationUtils.loadAnimation(
                holder.binding.value.context,
                androidx.appcompat.R.anim.abc_fade_in
            )
            holder.binding.background.startAnimation(fadeIn)
            holder.binding.background.visibility = View.VISIBLE
        }

        fun fadeOutBackground(holder: SudokuViewHolder) {
            val fadeOut = AnimationUtils.loadAnimation(
                holder.binding.value.context,
                androidx.appcompat.R.anim.abc_fade_out
            )
            holder.binding.background.startAnimation(fadeOut)
            holder.binding.background.visibility = View.INVISIBLE
        }

        fun numberToText(number: Int) = if (number == 0) "" else number.toString()
    }
}