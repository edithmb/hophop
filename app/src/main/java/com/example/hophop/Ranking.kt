package com.example.hophop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class Ranking : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        val btnegresarinicio2 = findViewById<Button>(R.id.btnregresaralinicio2)

        btnegresarinicio2.setOnClickListener {
            val intent = Intent(this, Inicio::class.java)
            startActivity(intent)
        }


    }

}
