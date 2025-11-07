package com.example.hophop

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnContinuar = findViewById<Button>(R.id.btnContinuar)

        btnContinuar.setOnClickListener {
                mostrarDialogoPersonalizado()


        }

    }
    private fun mostrarDialogoPersonalizado() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false) // No se puede cerrar tocando fuera
        dialog.setContentView(R.layout.mensaje_bienvenida)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

       val btnContinuar = dialog.findViewById<Button>(R.id.btnContinuar)
        btnContinuar.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, Eleccion::class.java))
       }

        dialog.show()
    }
}