package com.example.hophop

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Normas : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normas)

        val btnContinuarNormas = findViewById<Button>(R.id.btnContinuarN)

        btnContinuarNormas.setOnClickListener {
            val intent = Intent(this, Cuenta_Regresiva::class.java)
            startActivity(intent)
        }
    }
}