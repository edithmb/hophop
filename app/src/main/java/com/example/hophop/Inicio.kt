package com.example.hophop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Inicio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        val btnEmpezar = findViewById<Button>(R.id.btnEmpezar)

        btnEmpezar.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)

        }

    }
}