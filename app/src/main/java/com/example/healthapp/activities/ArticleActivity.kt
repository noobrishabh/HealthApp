package com.example.healthapp.activities

import android.os.Bundle
import com.example.healthapp.R
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView

class ArticleActivity : BaseActivity() {

    private lateinit var cardView: CardView
    private lateinit var textContent: TextView
    private lateinit var textCard: TextView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        // Initialize views
        cardView = findViewById(R.id.cardView)
        textContent = findViewById(R.id.textContent)
        textCard = findViewById(R.id.textCard)

        // Set click listener on the CardView
        cardView.setOnClickListener {
            toggleTextViewVisibility()
        }
    }

    private fun toggleTextViewVisibility() {
        if (cardView.visibility == View.VISIBLE) {
            textContent.visibility = View.VISIBLE
            cardView.visibility=View.INVISIBLE
            textCard.visibility=View.INVISIBLE
        } else {
            textContent.visibility = View.INVISIBLE

        }
    }
}