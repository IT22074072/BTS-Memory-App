package com.example.btsmemoryapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
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
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.clRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        clRoot = findViewById(R.id.clRoot)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves= findViewById(R.id.tvNumMoves)
        tvNumPairs = findViewById(R.id.tvNumPairs)

        setupBoard()
    }




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mi_refresh -> {
                //setup the game again
                if (memoryGame.getNumMoves() > 0 && !memoryGame.haveWonGame()){
                    showAlertDialog("Quit your current game?", null,View.OnClickListener{
                        setupBoard()
                    } )
                }else{
                    setupBoard()
                }
                return true

            }

            R.id.mi_new_size -> {
                showNewSizeDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showNewSizeDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)

        when (boardSize){
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbHard)
        }
        showAlertDialog("Choose new size", boardSizeView, View.OnClickListener {
            //Set a new value for the board size
            boardSize = when (radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            setupBoard()
        })
    }

    private fun showAlertDialog(title:String, view: View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK"){_,_->
                positiveClickListener.onClick(null)
        }.show()
    }


    private fun setupBoard() {
        when (boardSize){
            BoardSize.EASY -> {
                tvNumMoves.text = "Easy:4 x 2"
                tvNumPairs.text = "Pairs: 0 / 4"
            }
            BoardSize.MEDIUM -> {
                tvNumMoves.text = "Easy:6 x 3"
                tvNumPairs.text = "Pairs: 0 / 9"
            }
            BoardSize.HARD -> {
                tvNumMoves.text = "Easy:6 x 6"
                tvNumPairs.text = "Pairs: 0 / 12"
            }
        }
        tvNumPairs.setTextColor(ContextCompat.getColor(this, R.color.color_progress_none))
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
    @SuppressLint("RestrictedApi")
    private fun updateGameWithFlip(position: Int) {
        //Error handling
        if(memoryGame.haveWonGame()){
            //alert the user of an invalid move
            Snackbar.make(clRoot, "You already won!",Snackbar.LENGTH_LONG).show()
            return

        }
        if(memoryGame.isCardFaceUp(position)){
            //alert the user of an invalid move
            Snackbar.make(clRoot, "Invalid move!",Snackbar.LENGTH_SHORT).show()
            return
        }

        //Actually flip over the card
        if(memoryGame.flipCard(position)){
            Log.i(TAG, "Found a match! Number of pairs found: ${memoryGame.numPairsFound}")
            tvNumPairs.text = "Pairs: ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
            val color = ArgbEvaluator().evaluate(
                memoryGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                ContextCompat.getColor(this, R.color.color_progress_none),
                ContextCompat.getColor(this, R.color.color_progress_full)

            ) as Int
            tvNumPairs.setTextColor(color)

            if (memoryGame.haveWonGame()){
                Snackbar.make(clRoot, "You won! Congratulations.",Snackbar.LENGTH_LONG).show()
            }
        }
        tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()

    }


}