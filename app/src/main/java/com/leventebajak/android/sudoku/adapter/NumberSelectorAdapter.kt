package com.leventebajak.android.sudoku.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.leventebajak.android.sudoku.data.SelectableNumber
import com.leventebajak.android.sudoku.databinding.NumberSelectorCellBinding


class NumberSelectorAdapter(
    private val listener: NumberSelectedListener,
    private val numbers: List<SelectableNumber>,
    private var selectedNumber: Int,
    private val remainingEnabled: Boolean
) :
    RecyclerView.Adapter<NumberSelectorAdapter.NumberSelectorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberSelectorViewHolder {
        return NumberSelectorViewHolder(
            NumberSelectorCellBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: NumberSelectorViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }

        val number = payloads[0] as SelectableNumber

        if (remainingEnabled && number.value != 0)
            holder.binding.remaining.text = number.remaining.toString()
    }

    override fun onBindViewHolder(holder: NumberSelectorViewHolder, position: Int) {
        val number = numbers[position]

        holder.binding.number.text = numberToText(number.value)

        if (number.value != selectedNumber)
            holder.binding.background.visibility = View.INVISIBLE

        holder.binding.root.setOnClickListener {
            if (number.value != selectedNumber) {
                selectedNumber = number.value
                listener.onNumberSelected(number.value)
            }
        }

        if (remainingEnabled && number.value != 0)
            holder.binding.remaining.text = number.remaining.toString()
    }

    fun onCellValueChanged(previousValue: Int, newValue: Int) {
        // Update remaining numbers
        val previousNumber = numbers.find { it.value == previousValue }
        if (previousNumber != null && previousNumber.value != 0) {
            previousNumber.remaining++
            notifyItemChanged(numbers.indexOf(previousNumber), previousNumber)
        }

        val newNumber = numbers.find { it.value == newValue }
        if (newNumber != null && newNumber.value != 0) {
            newNumber.remaining--
            notifyItemChanged(numbers.indexOf(newNumber), newNumber)
        }
    }

    override fun getItemCount() = numbers.size

    interface NumberSelectedListener {
        fun onNumberSelected(number: Int)
    }

    inner class NumberSelectorViewHolder(val binding: NumberSelectorCellBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        fun fadeInBackground(holder: NumberSelectorViewHolder) {
            val fadeIn = AnimationUtils.loadAnimation(
                holder.binding.number.context,
                androidx.appcompat.R.anim.abc_fade_in
            )
            holder.binding.background.startAnimation(fadeIn)
            holder.binding.background.visibility = View.VISIBLE
        }

        fun fadeOutBackground(holder: NumberSelectorViewHolder) {
            val fadeOut = AnimationUtils.loadAnimation(
                holder.binding.number.context,
                androidx.appcompat.R.anim.abc_fade_out
            )
            holder.binding.background.startAnimation(fadeOut)
            holder.binding.background.visibility = View.INVISIBLE
        }

        fun textToNumber(text: String) = if (text == "X") 0 else text.toInt()

        fun numberToText(number: Int) = if (number == 0) "X" else number.toString()
    }
}