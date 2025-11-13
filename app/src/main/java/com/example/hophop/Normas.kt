package com.example.hophop

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Normas : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normas)

        val btnContinuarNormas = findViewById<Button>(R.id.btnContinuarN)
        val imageviewregresaraeleccion = findViewById<ImageView>(R.id.regresaraeleccion)

        btnContinuarNormas.setOnClickListener {
            mostrarCuentaRegresiva()
        }

        imageviewregresaraeleccion.setOnClickListener {
            val inten = Intent(this, Eleccion::class.java)
            startActivity(inten)
        }
    }

    private fun mostrarCuentaRegresiva() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.mensaje_cuentaregresiva)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val textoCountdown = dialog.findViewById<TextView>(R.id.textoCountdown)

        dialog.show()

        object : CountDownTimer(4000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val segundos = millisUntilFinished / 1000
                textoCountdown.text = segundos.toString()
            }

            override fun onFinish() {

                textoCountdown.text = "¡GO!"
                textoCountdown.setTextColor(Color.parseColor("#789345"))

                textoCountdown.postDelayed({
                                               dialog.dismiss()
                                               irAlJuego()
                                           }, 500)
            }
        }.start()
    }

    private fun irAlJuego() {
        val intent = Intent(this, Juego::class.java)
        startActivity(intent)
        finish()
    }
}