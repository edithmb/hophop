package com.example.hophop

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.util.Consumer
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Juego : AppCompatActivity() {

    private lateinit var img: ImageView
    private lateinit var zorroAnimation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)

        val layout = findViewById<ConstraintLayout>(R.id.juegolayout)
        val animalSeleccionado = intent.getIntExtra("animalSeleccionado", -1)

        val btnregresar = findViewById<Button>(R.id.btnRegresar)
        val txtviewprofileimage = findViewById<ImageView>(R.id.profile_image)

        val img = ImageView(this)

        val params = ConstraintLayout.LayoutParams(300, 300)
        img.y= 700f  // posición Y
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID

        img.layoutParams = params


        when (animalSeleccionado){
            R.id.tarjetaConejo -> img.setImageResource(R.drawable.conejo_quieto)
            R.id.tarjetaKoala -> img.setImageResource(R.drawable.koala_quieto)
            R.id.tarjetaZorro -> {
                img.setBackgroundResource(R.drawable.zorro_animation)
                zorroAnimation = img.background as AnimationDrawable
            }
            R.id.tarjetaGato -> img.setImageResource(R.drawable.gato_quieto)
            R.id.tarjetaDinosaurio-> img.setImageResource(R.drawable.dinosaurio_quieto)

        }
        layout.addView(img)

        layout.setOnClickListener {
            if (animalSeleccionado == R.id.tarjetaZorro) {
                if (!zorroAnimation.isRunning) {
                    zorroAnimation.start()
                }
            }
        }

        btnregresar.setOnClickListener {
            mostrarSalirJuego()
        }

        when (animalSeleccionado){
            R.id.tarjetaConejo -> txtviewprofileimage.setImageResource(R.drawable.conejo)
            R.id.tarjetaKoala -> txtviewprofileimage.setImageResource(R.drawable.koala)
            R.id.tarjetaZorro -> txtviewprofileimage.setImageResource(R.drawable.zorro)
            R.id.tarjetaGato -> txtviewprofileimage.setImageResource(R.drawable.gato)
            R.id.tarjetaDinosaurio-> txtviewprofileimage.setImageResource(R.drawable.dinosaurio)

        }
    }

    fun mostrarSalirJuego(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.mensaje_salir_juego)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val btnSalirSIJ = dialog.findViewById<Button>(R.id.btnSalirSIJ)
        val btnSalirNOJ = dialog.findViewById<Button>(R.id.btnSalirNOJ)
        val intent = Intent(this, Inicio::class.java)

        btnSalirSIJ?.setOnClickListener {
            dialog.dismiss()
            startActivity(intent)

        }

        btnSalirNOJ?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}