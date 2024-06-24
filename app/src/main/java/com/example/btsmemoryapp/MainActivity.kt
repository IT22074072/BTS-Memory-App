package com.example.btsmemoryapp

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btsmemoryapp.models.BoardSize
import com.example.btsmemoryapp.models.MemoryCard
import com.example.btsmemoryapp.models.MemoryGame
import com.example.btsmemoryapp.utils.DEFAULT_ICONS

class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MainActivity"
    }


    private lateinit var adapter: MemoryBoardAdapter
    private lateinit var rvBoard:RecyclerView
    private lateinit var tvNumMoves:TextView
    private lateinit var tvNumPairs:TextView

    private lateinit var memoryGame: MemoryGame

    private var boardSize:BoardSize = BoardSize.EASY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
        memoryGame.flipCard(position)
        adapter.notifyDataSetChanged()

    }


}