package com.example.hophop

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Eleccion : AppCompatActivity() {

    private lateinit var imgConejo: ImageView
    private lateinit var imgGato: ImageView
    private lateinit var imgZorro: ImageView
    private lateinit var imgDino: ImageView
    private lateinit var imgKoala: ImageView


    private var seleccionActual: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eleccion)

        val btnContinuarEleccion = findViewById<Button>(R.id.btnContinuarE)

        imgConejo = findViewById(R.id.tarjetaConejo)
        imgZorro = findViewById(R.id.tarjetaZorro)
        imgDino = findViewById(R.id.tarjetaDinosaurio)
        imgGato = findViewById(R.id.tarjetaGato)
        imgKoala = findViewById(R.id.tarjetaKoala)

        btnContinuarEleccion.setOnClickListener {
            val intent = Intent(this, Normas::class.java)
            startActivity(intent)
        }

        imgConejo.setOnClickListener { seleccionarImg(R.id.tarjetaConejo) }
        imgZorro.setOnClickListener { seleccionarImg(R.id.tarjetaZorro) }
        imgDino.setOnClickListener { seleccionarImg(R.id.tarjetaDinosaurio) }
        imgGato.setOnClickListener { seleccionarImg(R.id.tarjetaGato) }
        imgKoala.setOnClickListener { seleccionarImg(R.id.tarjetaKoala) }

    }

    private fun seleccionarImg(idSeleccionado:Int) {

        imgConejo.setImageResource(R.drawable.conejo_tarjeta)
        imgZorro.setImageResource(R.drawable.zorro_tarjeta)
        imgDino.setImageResource(R.drawable.dinosaurio_tarjeta)
        imgGato.setImageResource(R.drawable.gato_tarjeta)
        imgKoala.setImageResource(R.drawable.koala_tarjeta)

        when(idSeleccionado) {
            R.id.tarjetaConejo -> imgConejo.setImageResource(R.drawable.conejo_seleccionada)
            R.id.tarjetaZorro -> imgZorro.setImageResource(R.drawable.zorro_seleccionada)
            R.id.tarjetaDinosaurio -> imgDino.setImageResource(R.drawable.dinosaurio_seleccionada)
            R.id.tarjetaGato -> imgGato.setImageResource(R.drawable.gato_seleccionada)
            R.id.tarjetaKoala -> imgKoala.setImageResource(R.drawable.koala_seleccionada)
        }

        seleccionActual = idSeleccionado
    }
}