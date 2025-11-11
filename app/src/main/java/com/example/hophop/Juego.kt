package com.example.hophop

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.util.Consumer
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Juego : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)

        val layout = findViewById<ConstraintLayout>(R.id.juegolayout)
        val animalSeleccionado = intent.getIntExtra("animalSeleccionado", -1)

        val img = ImageView (this)
        img.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        when (animalSeleccionado){
            R.id.tarjetaConejo -> img.setImageResource(R.drawable.conejo)
            R.id.tarjetaKoala -> img.setImageResource(R.drawable.koala)
            R.id.tarjetaZorro -> img.setImageResource(R.drawable.zorro)
            R.id.tarjetaGato -> img.setImageResource(R.drawable.gato)
            R.id.tarjetaDinosaurio-> img.setImageResource(R.drawable.dinosaurio)

        }

        layout.addView(img)


    }
}