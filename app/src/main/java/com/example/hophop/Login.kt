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

        val btnContinuar = findViewById<Button>(R.id.btnContinuar)
        val editTextNombre = findViewById<EditText>(R.id.EditTextNombre)

        btnContinuar.setOnClickListener {

            val nombre = editTextNombre.text.toString().trim()

            if (nombre.isNotEmpty()) {

                val sharedPref = getSharedPreferences("DatosJugador", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString("nombreJugador", nombre)
                editor.apply()

                mostrarDialogoPersonalizado()
            } else {
                editTextNombre.error = "Por favor, escribe tu nombre completo"
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
        val nombreJugador = sharedPref.getString("nombreJugador", "Jugador")

        // 🔹 2. Referenciar el TextView dentro del layout del diálogo
        val txtBienvenida = dialog.findViewById<TextView>(R.id.txtBienvenida)

        // 🔹 3. Personalizar el texto
        txtBienvenida.text = "¡Bienvenid@, $nombreJugador!"

       val btnContinuar = dialog.findViewById<Button>(R.id.btnContinuar)
        btnContinuar.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, Eleccion::class.java))
       }

        dialog.show()
    }
}