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

class Inicio : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        val btnEmpezar = findViewById<Button>(R.id.btnEmpezar)
        val btnSalirI = findViewById<Button>(R.id.btnSalirI)

        btnEmpezar.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)

        }

        btnSalirI.setOnClickListener {
            mostrarMensajeSalir()
        }


    }

    private fun mostrarMensajeSalir(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.mensaje_salir)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val btnSalirSIA = dialog.findViewById<Button>(R.id.btnSalirSIA)
        val btnSalirNOA = dialog.findViewById<Button>(R.id.btnSalirNOA)

        btnSalirSIA?.setOnClickListener {
            dialog.dismiss()
            finishAffinity()
        }

        btnSalirNOA?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


}