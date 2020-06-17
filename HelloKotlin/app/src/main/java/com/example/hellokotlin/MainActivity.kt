package com.example.hellokotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button: Button = findViewById(R.id.button)
        val die1: TextView = findViewById(R.id.die1)
        val die2: TextView = findViewById(R.id.die2)

        button.setOnClickListener{
            val randomNumber1 = java.util.Random().nextInt(6)+1
            val randomNumber2 = java.util.Random().nextInt(6)+1
            Log.d("info", randomNumber1.toString())
            Log.d("info", randomNumber2.toString())

            die1.setText(randomNumber1.toString())
            die2.setText(randomNumber2.toString())


        }
    }
}
