package com.leventebajak.android.sudoku.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.leventebajak.android.sudoku.R
import com.leventebajak.android.sudoku.data.SudokuDatabase
import com.leventebajak.android.sudoku.databinding.FragmentMenuBinding
import com.leventebajak.sudokugenerator.SudokuGenerator
import kotlin.concurrent.thread


class MenuFragment : Fragment() {
    companion object {
        const val NO_GAME_ID = -1L
    }

    private lateinit var binding: FragmentMenuBinding

    private lateinit var database: SudokuDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = SudokuDatabase.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)

        thread {
            val latestGame = database.gameInfoDAO().getLast()

            activity?.runOnUiThread {
                if (latestGame == null)
                    binding.continueGame.isEnabled = false
                else
                    binding.continueGame.setOnClickListener {
                        this.findNavController()
                            .navigate(
                                R.id.action_menuFragment_to_gameFragment,
                                bundleOf(
                                    "gameID" to latestGame.id,
                                    "difficulty" to latestGame.difficulty.ordinal
                                )
                            )
                    }
            }
        }

        binding.newGame.setOnClickListener {
            PopupMenu(requireContext(), it).also { popup ->
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.easy -> this.findNavController()
                            .navigate(
                                R.id.action_menuFragment_to_gameFragment,
                                bundleOf(
                                    "difficulty" to SudokuGenerator.Difficulty.EASY.ordinal,
                                    "gameID" to NO_GAME_ID
                                )
                            )

                        R.id.medium -> this.findNavController()
                            .navigate(
                                R.id.action_menuFragment_to_gameFragment,
                                bundleOf(
                                    "difficulty" to SudokuGenerator.Difficulty.MEDIUM.ordinal,
                                    "gameID" to NO_GAME_ID
                                )
                            )

                        R.id.hard -> this.findNavController()
                            .navigate(
                                R.id.action_menuFragment_to_gameFragment,
                                bundleOf(
                                    "difficulty" to SudokuGenerator.Difficulty.HARD.ordinal,
                                    "gameID" to NO_GAME_ID
                                )
                            )
                        R.id.very_hard -> this.findNavController()
                            .navigate(
                                R.id.action_menuFragment_to_gameFragment,
                                bundleOf(
                                    "difficulty" to SudokuGenerator.Difficulty.VERY_HARD.ordinal,
                                    "gameID" to NO_GAME_ID
                                )
                            )
                    }
                    true
                }
                popup.menuInflater.inflate(R.menu.difficulty_popup, popup.menu)
                popup.show()
            }
        }

        binding.settings.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_settingsFragment)
        }

        return binding.root
    }
}