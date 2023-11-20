package com.leventebajak.android.sudoku.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.leventebajak.android.sudoku.R
import com.leventebajak.android.sudoku.adapter.NumberSelectorAdapter
import com.leventebajak.android.sudoku.adapter.SudokuBoardAdapter
import com.leventebajak.android.sudoku.data.GameInfo
import com.leventebajak.android.sudoku.data.SelectableNumber
import com.leventebajak.android.sudoku.data.SudokuCell
import com.leventebajak.android.sudoku.data.SudokuDatabase
import com.leventebajak.android.sudoku.databinding.FragmentGameBinding
import com.leventebajak.sudokugenerator.SudokuGenerator
import kotlin.concurrent.thread


class GameFragment : Fragment(), SudokuBoardAdapter.CellClickListener,
    NumberSelectorAdapter.NumberSelectedListener {
    companion object {
        private const val initialSelectedNumber = 0
    }

    private lateinit var binding: FragmentGameBinding

    private lateinit var sudokuBoardAdapter: SudokuBoardAdapter
    private lateinit var numberSelectorAdapter: NumberSelectorAdapter

    private lateinit var database: SudokuDatabase

    private var selectedNumber: Int = -1

    private var running = true

    private var gameID: Long? = null
    private lateinit var gameInfo: GameInfo

    private var highlightingEnabled = true
    private var remainingEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Back button should take the user back to the menu
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_gameFragment_to_menuFragment)
        }

        database = SudokuDatabase.getDatabase(requireContext())

        // If the fragment is recreated, the selected number and the game ID should be restored
        selectedNumber = savedInstanceState?.getInt("selectedNumber") ?: initialSelectedNumber

        gameID = savedInstanceState?.getLong("gameID") ?: arguments?.getLong("gameID")

        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        highlightingEnabled = preferences?.getBoolean("highlights", highlightingEnabled)
            ?: highlightingEnabled
        remainingEnabled =
            preferences?.getBoolean("remaining", remainingEnabled) ?: remainingEnabled
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false)

        val cells = mutableListOf<SudokuCell>()

        if (gameID != null && gameID != MenuFragment.NO_GAME_ID) {
            // Load the game from the database
            thread {
                val databaseGameInfo = database.gameInfoDAO().getByID(gameID!!)
                    ?: throw IllegalStateException("Game with ID $gameID not found")

                gameInfo = databaseGameInfo
                cells.addAll(
                    database.sudokuCellDAO().getAllByGameID(gameInfo.id!!)
                        .map { SudokuCell(it) }
                )

                // Sort cells by row then by column (just to be sure)
                cells.sortWith(compareBy({ it.data.row }, { it.data.column }))
            }.join()
        } else {
            // Generate a new game
            thread {
                // Delete previous games (the related cells will be deleted automatically)
                for (game in database.gameInfoDAO().getAll())
                    database.gameInfoDAO().delete(game)

                if (database.gameInfoDAO().getCount() != 0)
                    throw IllegalStateException("Cell have not been deleted")
            }.join()

            gameInfo = GameInfo(
                null,
                SudokuGenerator.Difficulty.values()[arguments?.getInt("difficulty")!!],
                0
            )

            thread {
                gameID = database.gameInfoDAO().insert(gameInfo)
                gameInfo.id = gameID

                val sudoku = SudokuGenerator.generate(gameInfo.difficulty)
                for (row in 0 until 9)
                    for (col in 0 until 9) {
                        val cell = SudokuCell(
                            sudoku.clues[row, col],
                            sudoku.solutions[0][row, col],
                            row,
                            col,
                            gameID!!
                        )
                        cell.data.id = database.sudokuCellDAO().insert(cell.data)
                        cells.add(cell)
                    }
            }.join()
        }

        // Initialize recycler views
        initSudokuBoard(cells)
        initNumberSelector(cells)

        // Start timer
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                if (running) {
                    binding.time.text =
                        String.format("%02d:%02d", gameInfo.seconds / 60, gameInfo.seconds % 60)
                    gameInfo.seconds++
                }
                handler.postDelayed(this, 1000)
            }
        })

        binding.backToMenu.setOnClickListener {
            this.findNavController().navigate(R.id.action_gameFragment_to_menuFragment)
        }

        binding.difficulty.text = when (gameInfo.difficulty) {
            SudokuGenerator.Difficulty.EASY -> getString(R.string.easy).uppercase()
            SudokuGenerator.Difficulty.MEDIUM -> getString(R.string.medium).uppercase()
            SudokuGenerator.Difficulty.HARD -> getString(R.string.hard).uppercase()
            SudokuGenerator.Difficulty.VERY_HARD -> getString(R.string.very_hard).uppercase()
        }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong("gameID", gameID!!)
        outState.putInt("selectedNumber", selectedNumber)
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        running = false
        thread {
            database.gameInfoDAO().update(gameInfo)
        }
    }

    override fun onResume() {
        super.onResume()
        running = true
    }

    private fun initSudokuBoard(cells: List<SudokuCell>) {
        binding.sudokuBoard.layoutManager = object : GridLayoutManager(context, 9) {
            override fun canScrollVertically() = false
        }

        sudokuBoardAdapter = SudokuBoardAdapter(this, cells, selectedNumber, highlightingEnabled)
        binding.sudokuBoard.adapter = sudokuBoardAdapter

        val boardSize = minOf(
            resources.displayMetrics.widthPixels,
            resources.displayMetrics.heightPixels
        ) * 9 / 10

        binding.sudokuBoard.layoutParams.width = boardSize
        binding.sudokuBoard.layoutParams.height = boardSize
    }

    private fun initNumberSelector(cells: MutableList<SudokuCell>) {
        val numbers = (1..9).toMutableList()
        numbers.add(0)

        val numberCount = IntArray(10)
        for (cell in cells)
            numberCount[cell.data.value]++

        val selectableNumbers = numbers.map {
            SelectableNumber(
                it,
                if (it == 0) 0 else 9 - numberCount[it],
                selectedNumber == it
            )
        }.toList()

        binding.numberSelector.layoutManager = object : GridLayoutManager(context, 5) {
            override fun canScrollVertically() = false
        }

        numberSelectorAdapter =
            NumberSelectorAdapter(this, selectableNumbers, selectedNumber, remainingEnabled)
        binding.numberSelector.adapter = numberSelectorAdapter
    }

    override fun onCellValueChanged(item: SudokuCell, previousValue: Int) {
        thread {
            database.sudokuCellDAO().update(item.data)
        }
        if (sudokuBoardAdapter.isSolved()) {
            findNavController().navigate(
                R.id.action_gameFragment_to_gameOverFragment,
                bundleOf(
                    "difficulty" to gameInfo.difficulty.ordinal,
                    "seconds" to gameInfo.seconds
                )
            )
        }

        numberSelectorAdapter.onCellValueChanged(previousValue, item.data.value)
    }

    override fun getSelectedNumber() = selectedNumber

    override fun onNumberSelected(number: Int) {
        val previousNumber = selectedNumber

        selectedNumber = number

        for (i in 0 until numberSelectorAdapter.itemCount) {
            val holder = binding.numberSelector.findViewHolderForAdapterPosition(i)
                    as NumberSelectorAdapter.NumberSelectorViewHolder
            val value = NumberSelectorAdapter.textToNumber(holder.binding.number.text.toString())

            if (value == selectedNumber)
                NumberSelectorAdapter.fadeInBackground(holder)
            else if (value == previousNumber)
                NumberSelectorAdapter.fadeOutBackground(holder)
        }

        if (highlightingEnabled)
            changeHighlights(previousNumber, selectedNumber)
    }

    private fun changeHighlights(previousNumber: Int, newNumber: Int) {
        for (i in 0 until sudokuBoardAdapter.itemCount) {
            val holder = binding.sudokuBoard.findViewHolderForAdapterPosition(i)
                    as SudokuBoardAdapter.SudokuViewHolder
            val value =
                if (holder.binding.value.text.toString() == "") 0
                else holder.binding.value.text.toString().toInt()

            if (previousNumber != SudokuCell.EMPTY && value == previousNumber)
                SudokuBoardAdapter.fadeOutBackground(holder)
            else if (newNumber != SudokuCell.EMPTY && value == newNumber)
                SudokuBoardAdapter.fadeInBackground(holder)
        }
    }
}