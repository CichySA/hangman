package com.example.hangman

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject

const val DEFAULT_WORD_LENGTH = 5

class MainActivity : AppCompatActivity() {
    var wordLen: Int = DEFAULT_WORD_LENGTH
    private val words: HashMap<String, Int> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val lengthBar = findViewById<SeekBar>(R.id.lengthBar)
        val wordLengthView = findViewById<TextView>(R.id.app_title)
        wordLengthView.text = getString(R.string.word_length) + DEFAULT_WORD_LENGTH.toString()
        try {
            words.clear()
            val str = resources.openRawResource(R.raw.words_dictionary).bufferedReader()
                .use { it.readText() }
            JSONObject(str).keys().forEachRemaining { words[it] = it.length }
        } catch (e: Exception) {
            // TODO Toast warning player
        }

        lengthBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(lengthBar: SeekBar?, progress: Int, fromUser: Boolean) {
                wordLen = lengthBar?.progress?.plus(2) ?: DEFAULT_WORD_LENGTH
                wordLengthView.text = getString(R.string.word_length) + wordLen.toString()
            }

            override fun onStartTrackingTouch(lengthBar: SeekBar?) {
                //
            }

            override fun onStopTrackingTouch(lengthBar: SeekBar?) {
                //
            }

        })
        val nav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        nav.setOnNavigationItemSelectedListener(object :
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                return if (item.itemId == R.id.startMenuItem) {
                    startGame()
                    true
                } else item.itemId == R.id.infoMenuItem
            }

        })

    }

    fun startGame() {
        val c = words.filterValues { it == wordLen }.keys
        val word = c.random()
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("EXTRA_WORD", word)
        }
        startActivity(intent)
    }
}