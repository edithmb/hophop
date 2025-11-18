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
    private lateinit var animalAnimation: AnimationDrawable
    private lateinit var fondo1: ImageView
    private lateinit var fondo2: ImageView
    private lateinit var layout: ConstraintLayout

    private val handler = Handler(Looper.getMainLooper())
    private var estaMoviendo = false
    private val velocidadScroll = 100f
    private var anchoFondo = 0
    private val obstaculos = mutableListOf<Obstaculo>()
    private val tiempoGeneracionObstaculos = 2000L
    private val handlerObstaculos = Handler(Looper.getMainLooper())
    private var juegoActivo = false
    private var puntuacion = 0

    private var generadorObstaculos = object : Runnable {
        override fun run() {
            if (juegoActivo){
                generarObstaculo()
                handlerObstaculos.postDelayed(this,tiempoGeneracionObstaculos)
            }
        }
    }

    private var verificadorColisiones = object : Runnable {
        override fun run() {
            if (juegoActivo){
                verificarColisiones()
                handler.postDelayed(this,50)
            }
        }
    }


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

                moverObstaculos()

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

        moverFondo()

        img = ImageView(this)
        val params = ConstraintLayout.LayoutParams(300, 300)
        img.y= 700f  // posición Y
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID

        img.layoutParams = params


        when (animalSeleccionado){
            R.id.tarjetaConejo -> {
                img.setBackgroundResource(R.drawable.conejo_animation)
            }
            R.id.tarjetaKoala -> {
                img.setBackgroundResource(R.drawable.koala_animation)
            }
            R.id.tarjetaZorro -> {
                img.setBackgroundResource(R.drawable.zorro_animation)
            }
            R.id.tarjetaGato -> {
                img.setBackgroundResource(R.drawable.gato_animation)
            }
            R.id.tarjetaDinosaurio-> {
                img.setBackgroundResource(R.drawable.dinosaurio_animation)
            }
        }

        animalAnimation = img.background as AnimationDrawable
        layout.addView(img)

        layout.setOnClickListener {
            if (!animalAnimation.isRunning) {
                iniciarJuego()
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

    private fun iniciarJuego(){
        animalAnimation.start()
        estaMoviendo = true
        juegoActivo = true

        handler.post(movimientoRunnable)
        handlerObstaculos.post(generadorObstaculos)
    }

    private fun generarObstaculo(){
        val obstaculoVista = ImageView(this)

        val params = ConstraintLayout.LayoutParams(200,200)
        obstaculoVista.layoutParams = params

        val displayMetrics = resources.displayMetrics
        obstaculoVista.x = displayMetrics.widthPixels.toFloat()

        val posicionesY = listOf(
            750f
                                )

        obstaculoVista.y = posicionesY.random()

        val tiposDisponibles = listOf (
            TipoObstaculo.fruta,
            TipoObstaculo.dulce,
            TipoObstaculo.arbusto )

        val tipoElegido = tiposDisponibles.random()

        val puntos = when (tipoElegido) {
            TipoObstaculo.fruta -> {
                val frutas = listOf(
                    R.drawable.manzana,
                    R.drawable.naranja,
                    R.drawable.fresa )
                obstaculoVista.setImageResource(frutas.random())
                10
            }

            TipoObstaculo.dulce -> {
                val dulces = listOf(
                    R.drawable.paleta,
                    R.drawable.caramelo,
                    R.drawable.chocolate )
                obstaculoVista.setImageResource(dulces.random())
                -5
            }

            TipoObstaculo.arbusto -> {
                obstaculoVista.setBackgroundResource(R.drawable.arbusto)
                0
            }
        }

        val nuevoObstaculo = Obstaculo(obstaculoVista, tipoElegido, puntos)
        obstaculos.add(nuevoObstaculo)

        layout.addView(obstaculoVista)
    }

    private fun moverObstaculos(){
        val obstaculosAEliminar = mutableListOf<Obstaculo>()

        for (obstaculo in obstaculos){
            obstaculo.vista.x -= velocidadScroll

            if(obstaculo.vista.x + obstaculo.vista.width < 0){
                obstaculosAEliminar.add(obstaculo)
            }
        }

        for (obstaculo in obstaculosAEliminar){
            layout.removeView(obstaculo.vista)
            obstaculos.remove(obstaculo)
        }

    }

    private fun verificarColisiones(){}

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