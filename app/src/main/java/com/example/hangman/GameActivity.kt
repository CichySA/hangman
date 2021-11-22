package com.example.hangman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat

class GameActivity : AppCompatActivity() {
    val maxMoves = 10

    private var currentMove = 0
    private var targetWord: String? = null
    private var revealedWord: CharArray? = null
    private var uniqueLetterSet: MutableSet<Char>? = null
    private val images = intArrayOf(
        R.drawable.image0of10, R.drawable.image1of10, R.drawable.image2of10,
        R.drawable.image3of10, R.drawable.image4of10, R.drawable.image5of10, R.drawable.image6of10,
        R.drawable.image7of10, R.drawable.image8of10, R.drawable.image9of10, R.drawable.image10of10
    )
    private var hangmanView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val buttonRowLayout = findViewById<LinearLayout>(R.id.buttonRowLayout)
        val inflater = LayoutInflater.from(this)
        hangmanView = findViewById(R.id.hangmanImage)
        inflater.inflate(R.layout.keyboard, buttonRowLayout)
        iterateButtons(::addButtonListener)
        buttonRowLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                buttonRowLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                var row = buttonRowLayout.getChildAt(1) as LinearLayout
                var paddingH = row.width * 1 / 10
                row.setPadding(paddingH / 2, 0, paddingH / 2, 0)
                row = buttonRowLayout.getChildAt(2) as LinearLayout
                paddingH = row.width * 3 / 10
                row.setPadding(paddingH / 2, 0, paddingH / 2, 0)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        currentMove = 0
        targetWord = intent.getStringExtra("EXTRA_WORD")
        uniqueLetterSet = targetWord?.toCharArray()?.toMutableSet()
        revealedWord = CharArray(targetWord?.length ?: 0) { '*' }
        val wordView = findViewById<TextView>(R.id.wordView)
        wordView.text = revealedWord!!.joinToString(separator = "", prefix = "", postfix = "")
        hangmanView?.setImageResource(images[0])
    }

    private fun iterateButtons(function: (AppCompatButton) -> Unit) {
        val buttonRowLayout: LinearLayout = findViewById(R.id.buttonRowLayout)
        for (i in 0 until buttonRowLayout.childCount) {
            val row: LinearLayout = buttonRowLayout.getChildAt(i) as LinearLayout
            for (j in 0 until row.childCount) {
                val btn = row.getChildAt(j) as AppCompatButton
                function(btn)
            }
        }

    }

    private fun lockButton(btn: AppCompatButton) {
        btn.isClickable = false
        if (targetWord?.contains(btn.text.first()) == true) {
            btn.setBackgroundColor(getColor(R.color.design_default_color_secondary))
        } else {
            btn.setBackgroundColor(getColor(R.color.design_default_color_error))
        }
    }

    private fun addButtonListener(btn: AppCompatButton) {
        val revealedWordView = findViewById<TextView>(R.id.wordView)
        btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val c = btn.text.first()
                // TODO what if null?
                if (uniqueLetterSet?.contains(c) == true) {
                    btn.setBackgroundColor(
                        ContextCompat.getColor(
                            this@GameActivity,
                            R.color.design_default_color_secondary
                        )
                    )

                    uniqueLetterSet!!.remove(c)
                    var startInd = targetWord!!.indexOf(c, 0)
                    while (startInd != -1) {
                        revealedWord!![startInd] = c
                        startInd = targetWord!!.indexOf(c, startInd + 1)
                    }
                    // draw letters
                    revealedWordView.text =
                        revealedWord!!.joinToString(separator = "", prefix = "", postfix = "")
                            .uppercase()
                    // end game
                    if (uniqueLetterSet.isNullOrEmpty()) {
                        iterateButtons { btn -> btn.isClickable = false }
                        //TODO notify user
                    }
                } else {
                    btn.setBackgroundColor(
                        ContextCompat.getColor(
                            this@GameActivity,
                            R.color.design_default_color_error
                        )
                    )
                    currentMove++
                    hangmanView?.setImageResource(images[if (currentMove > maxMoves) maxMoves else currentMove])
                    if (currentMove >= maxMoves) {

                        revealedWordView.text = targetWord?.uppercase()
                        iterateButtons(this@GameActivity::lockButton)
                        //TODO notify user
                    }
                }
                btn.isClickable = false

            }
        })
    }
}

