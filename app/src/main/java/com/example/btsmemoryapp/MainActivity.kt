package com.example.btsmemoryapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btsmemoryapp.models.BoardSize
import com.example.btsmemoryapp.models.MemoryGame
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MainActivity"
    }


    private lateinit var adapter: MemoryBoardAdapter
    private lateinit var rvBoard:RecyclerView
    private lateinit var tvNumMoves:TextView
    private lateinit var tvNumPairs:TextView
    private lateinit var clRoot:ConstraintLayout

    private lateinit var memoryGame: MemoryGame

    private var boardSize:BoardSize = BoardSize.EASY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.clRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        clRoot = findViewById(R.id.clRoot)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves= findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)



        memoryGame = MemoryGame(boardSize)


        adapter = MemoryBoardAdapter(this, boardSize, memoryGame.cards, object :MemoryBoardAdapter.CardClickListener{
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)
            }

        })

        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }

    private fun updateGameWithFlip(position: Int) {
        //Error handling
        if(memoryGame.haveWonGame()){
            //alert the user of an invalid move
            Snackbar.make(clRoot, "You already won!",Snackbar.LENGTH_LONG).show()
            return

        }
        if(memoryGame.isCardFaceUp(position)){
            //alert the user of an invalid move
            Snackbar.make(clRoot, "Invalid move!",Snackbar.LENGTH_LONG).show()
            return
        }

        //Actually flip over the card
        if(memoryGame.flipCard(position)){
            Log.i(TAG, "Found a match! Number of pairs found: ${memoryGame.numPairsFound}")
        }
        adapter.notifyDataSetChanged()

    }


}