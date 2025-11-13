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

class Loginpart2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginparte2)

        val btnContinuar2 = findViewById<Button>(R.id.btnContinuar2)
        val editTextApodo = findViewById<EditText>(R.id.EditTextApodo)

        btnContinuar2.setOnClickListener {

            val nombre = editTextApodo.text.toString().trim()

            if (nombre.isNotEmpty()) {

                val sharedPref = getSharedPreferences("DatosJugador", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString("apodoJugador", nombre)
                editor.apply()

                mostrarDialogoPersonalizado()
            } else {
                editTextApodo.error = "Por favor, escribe tu apodo"
            }

        }

    }
    private fun mostrarDialogoPersonalizado() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.mensaje_bienvenida)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val sharedPref = getSharedPreferences("DatosJugador", Context.MODE_PRIVATE)
        val apodoJugador = sharedPref.getString("apodoJugador", "Jugador")

        val txtBienvenida = dialog.findViewById<TextView>(R.id.txtBienvenida)

        txtBienvenida.text = "¡Bienvenid@, ${apodoJugador}!"

        val btnContinuar3 = dialog.findViewById<Button>(R.id.btnContinuar3)
        btnContinuar3.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, Eleccion::class.java))
        }

        dialog.show()
    }
}