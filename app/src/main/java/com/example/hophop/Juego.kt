package com.example.hophop

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints

class Juego : AppCompatActivity() {

    private lateinit var img: ImageView
    private lateinit var zorroAnimation: AnimationDrawable
    private lateinit var fondo1: ImageView
    private lateinit var fondo2: ImageView
    private lateinit var layout: ConstraintLayout

    private val handler = Handler(Looper.getMainLooper())
    private var estaMoviendo = false
    private val velocidadScroll = 100f
    private var anchoFondo = 0


    private var movimientoRunnable = object : Runnable {
        override fun run() {
            if (estaMoviendo){
                fondo1.x -= velocidadScroll
                fondo2.x -= velocidadScroll

                if (fondo1.x + anchoFondo <= 0) {
                    fondo1.x = fondo2.x + anchoFondo
                }
                if (fondo2.x + anchoFondo <= 0) {
                    fondo2.x = fondo1.x + anchoFondo
                }
                handler.postDelayed(this,50)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)
        layout = findViewById(R.id.juegolayout)


        val animalSeleccionado = intent.getIntExtra("animalSeleccionado", -1)
        val btnregresar = findViewById<Button>(R.id.btnRegresar)
        val txtviewprofileimage = findViewById<ImageView>(R.id.profile_image)

        img = ImageView(this)
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

        layout.setOnClickListener {
            if (animalSeleccionado == R.id.tarjetaZorro) {
                if (!zorroAnimation.isRunning) {
                    zorroAnimation.start()
                    estaMoviendo = true
                    handler.post(movimientoRunnable)
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

        moverFondo()
        layout.addView(img)
    }

    private fun moverFondo(){

        val displayMetrics = resources.displayMetrics
        anchoFondo = displayMetrics.widthPixels

        fondo1 = ImageView(this)
        val paramsFondo1 = ConstraintLayout.LayoutParams(
            anchoFondo,
            ConstraintLayout.LayoutParams.MATCH_PARENT
                                                        )
        paramsFondo1.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        paramsFondo1.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        fondo1.layoutParams = paramsFondo1
        fondo1.setImageResource(R.drawable.fondo_juego1)
        fondo1.scaleType = ImageView.ScaleType.FIT_XY
        fondo1.x = 0f

        fondo2 = ImageView(this)
        val paramsFondo2 = ConstraintLayout.LayoutParams(
            anchoFondo,
            ConstraintLayout.LayoutParams.MATCH_PARENT
                                                        )
        paramsFondo2.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        paramsFondo2.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        fondo2.layoutParams = paramsFondo2
        fondo2.setImageResource(R.drawable.fondo_juego2)
        fondo2.scaleType = ImageView.ScaleType.FIT_XY
        fondo2.x = anchoFondo.toFloat()

        layout.addView(fondo1)
        layout.addView(fondo2)
    }

    override fun onDestroy() {
        super.onDestroy()
        estaMoviendo = false
        handler.removeCallbacks (movimientoRunnable)
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