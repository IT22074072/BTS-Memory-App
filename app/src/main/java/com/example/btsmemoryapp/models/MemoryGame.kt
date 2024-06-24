package com.example.btsmemoryapp.models

import com.example.btsmemoryapp.utils.DEFAULT_ICONS

class MemoryGame(private val boardSize: BoardSize){
    fun flipCard(position: Int) {
        val card = cards[position]
        card.isFaceUp = !card.isFaceUp

    }

    val cards:List<MemoryCard>
    val numPairsFound = 0

    init {
        val chosenImages = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedImages = (chosenImages + chosenImages).shuffled()
        cards = randomizedImages.map{ MemoryCard(it)}

    }

}