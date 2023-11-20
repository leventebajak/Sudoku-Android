package com.leventebajak.android.sudoku.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.leventebajak.android.sudoku.R
import com.leventebajak.android.sudoku.data.PersonalBest
import com.leventebajak.android.sudoku.data.SudokuDatabase
import com.leventebajak.android.sudoku.databinding.FragmentGameOverBinding
import com.leventebajak.sudokugenerator.SudokuGenerator
import kotlin.concurrent.thread

class GameOverFragment : Fragment() {
    private lateinit var binding: FragmentGameOverBinding

    private lateinit var database: SudokuDatabase

    private lateinit var difficulty: SudokuGenerator.Difficulty

    private var seconds: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_gameOverFragment_to_menuFragment)
        }

        database = SudokuDatabase.getDatabase(requireContext())

        difficulty = SudokuGenerator.Difficulty.values()[
            savedInstanceState?.getInt("difficulty") ?: arguments?.getInt("difficulty")!!]
        seconds = savedInstanceState?.getInt("seconds") ?: arguments?.getInt("seconds")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameOverBinding.inflate(inflater, container, false)

        var personalBest: PersonalBest? = null
        thread {
            personalBest = database.personalBestDAO().getByDifficulty(difficulty)
        }.join()
        val newPB = (personalBest?.seconds ?: Int.MAX_VALUE) > seconds

        if (newPB) {
            thread {
                if (personalBest != null)
                    database.personalBestDAO().delete(personalBest!!)
                database.personalBestDAO().insert(PersonalBest(null, difficulty, seconds))
            }
            binding.pbTitle.text = getString(R.string.new_pb)
        } else {
            binding.pbTime.text =
                String.format("%02d:%02d", personalBest!!.seconds / 60, personalBest!!.seconds % 60)
        }

        binding.difficulty.text = when (difficulty) {
            SudokuGenerator.Difficulty.EASY -> getString(R.string.easy).uppercase()
            SudokuGenerator.Difficulty.MEDIUM -> getString(R.string.medium).uppercase()
            SudokuGenerator.Difficulty.HARD -> getString(R.string.hard).uppercase()
            SudokuGenerator.Difficulty.VERY_HARD -> getString(R.string.very_hard).uppercase()
        }

        binding.yourTime.text = String.format("%02d:%02d", seconds / 60, seconds % 60)

        thread {
            // Delete previous games (the related cells will be deleted automatically)
            val games = database.gameInfoDAO().getAll()
            for (game in games)
                database.gameInfoDAO().delete(game)
            if (database.sudokuCellDAO().getCount() != 0)
                throw IllegalStateException("There are still cells in the database")
        }

        binding.backToMenu.setOnClickListener {
            this.findNavController().navigate(R.id.action_gameOverFragment_to_menuFragment)
        }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("difficulty", difficulty.ordinal)
        outState.putInt("seconds", seconds)
        super.onSaveInstanceState(outState)
    }
}