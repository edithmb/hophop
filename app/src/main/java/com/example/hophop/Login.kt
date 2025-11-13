package com.example.hophop

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnContinuar1 = findViewById<Button>(R.id.btnContinuar1)
        val editTextNombre = findViewById<EditText>(R.id.EditTextNombre)

        btnContinuar1.setOnClickListener {

            val nombre = editTextNombre.text.toString().trim()

            if (nombre.isNotEmpty()) {

                val sharedPref = getSharedPreferences("DatosJugador", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString("nombreJugador", nombre)
                editor.apply()

                startActivity(Intent(this, Loginpart2 ::class.java))
            } else {
                editTextNombre.error = "Por favor, escribe tu nombre completo"
            }

        }

    }
}