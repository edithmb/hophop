package com.example.hophop

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.media.Image
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.util.Log.e
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class Juego : AppCompatActivity() {
    private lateinit var panelSuperior: LinearLayout
    private lateinit var txtPuntuacion: TextView
    private lateinit var txtNombreUsuario: TextView
    private lateinit var imgVidas: ImageView

    private var nombreUsuario: String = ""

    private var vidasActuales: Int = 3
    private var vidasMaximas = 3

    //variables de imagenes
    private lateinit var img: ImageView
    private lateinit var animalAnimation: AnimationDrawable
    private lateinit var fondo1: ImageView
    private lateinit var fondo2: ImageView
    private lateinit var layout: ConstraintLayout

    private val handler = Handler(Looper.getMainLooper())

    private val intervaloFrame = 50L
    private var anchoFondo = 0
    private val obstaculos = mutableListOf<Obstaculo>()
    private val tiempoGeneracionObstaculos = 4000L
    private val handlerObstaculos = Handler(Looper.getMainLooper())
    private var juegoActivo = false
    private var estaMoviendo = false
    private var puntuacion = 0


    //variables de salto
    private var velocidadVertical: Float = 0f
    private val gravedad: Float = 75f
    private val fuerzaSalto: Float = -15f
    private val posicionYSuelo: Float = 700f
    private var estaSaltando: Boolean = false

    //variables de velocidad
    private var velocidadScrollActual = 15f
    private val velocidadScrollMaxima = 30f
    private val incrementoVelocidad = 0.5f
    private var obstaculosGenerados = 0

    //variables de manejo de datos JSON
    private lateinit var gameDataManager: GameDataManager
    private var animalNombre: String = "Desconocido"
    private var frutasComidas = 0
    private var verdurasComidas = 0
    private var dulcesComidos = 0
    private var obstaculosEvitados = 0
    private var tiempoDeJuego = 0
    private var temporizadorActivo = false



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
                fondo1.x -= velocidadScrollActual
                fondo2.x -= velocidadScrollActual

                if (fondo1.x + anchoFondo <= 0) {
                    fondo1.x = fondo2.x + anchoFondo
                }
                if (fondo2.x + anchoFondo <= 0) {
                    fondo2.x = fondo1.x + anchoFondo
                }

                moverObstaculos()
                aplicarGravedad()

                handler.postDelayed(this,50)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)

        gameDataManager = GameDataManager(this)



        layout = findViewById(R.id.juegolayout)

        panelSuperior = findViewById(R.id.panelSuperior)
        txtPuntuacion = findViewById(R.id.txtPuntuacion)
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario)
        imgVidas = findViewById(R.id.imgVidas)

        vidasActuales = vidasMaximas
        actualizarVidas()

        var sharedPref = getSharedPreferences("DatosJugador", Context.MODE_PRIVATE)
        nombreUsuario = sharedPref.getString("apodoJugador", "Jugador") ?: "Jugador"
        txtNombreUsuario.text = nombreUsuario


        val animalSeleccionado = intent.getIntExtra("animalSeleccionado", -1)
        val btnregresar = findViewById<Button>(R.id.btnRegresar)
        val txtviewprofileimage = findViewById<ImageView>(R.id.profile_image)

        moverFondo()

        img = ImageView(this)
        val params = ConstraintLayout.LayoutParams(300, 300)
        img.y= posicionYSuelo  // posición Y
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID

        img.layoutParams = params


        when (animalSeleccionado){
            R.id.tarjetaConejo -> {
                img.setBackgroundResource(R.drawable.conejo_animation)
                animalNombre = "conejo"
            }
            R.id.tarjetaKoala -> {
                img.setBackgroundResource(R.drawable.koala_animation)
                animalNombre = "Koala"
            }
            R.id.tarjetaZorro -> {
                img.setBackgroundResource(R.drawable.zorro_animation)
                animalNombre = "Zorro"
            }
            R.id.tarjetaGato -> {
                img.setBackgroundResource(R.drawable.gato_animation)
                animalNombre = "Gato"
            }
            R.id.tarjetaDinosaurio-> {
                img.setBackgroundResource(R.drawable.dinosaurio_animation)
                animalNombre = "Dinosaurio"
            }
        }

        animalAnimation = img.background as AnimationDrawable
        layout.addView(img)

        layout.setOnClickListener {
            if (!animalAnimation.isRunning) {
                reiniciarContadores()
                iniciarTemporizador()
                iniciarJuego()
            }
            else {
                saltar()
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

    private fun resetearMetricas(){
        frutasComidas = 0
        verdurasComidas = 0
        dulcesComidos = 0
        obstaculosEvitados = 0

    }

    private fun iniciarJuego(){
        animalAnimation.start()
        estaMoviendo = true
        handler.post(movimientoRunnable)

        juegoActivo = true
        handlerObstaculos.post(generadorObstaculos)

        handler.post(verificadorColisiones)
    }

    private fun reiniciarJuego(){

        detenerJuego()

        for(obstaculo in obstaculos){
            layout.removeView(obstaculo.vista)
        }

        obstaculos.clear()

        puntuacion = 0

        velocidadScrollActual = 15f
        obstaculosGenerados = 0

        actualizarPuntuacion()
        mostrarPanel()
        img.y = posicionYSuelo
        velocidadVertical = 0f
        estaSaltando = false
        iniciarJuego()
    }

    private fun detenerJuego(){
        juegoActivo = false
        estaMoviendo = false

        if(animalAnimation.isRunning){
            animalAnimation.stop()
        }

        handler.removeCallbacks(movimientoRunnable)
        handler.removeCallbacks(verificadorColisiones)
        handlerObstaculos.removeCallbacks(generadorObstaculos)
    }

    private fun saltar(){
        if(!estaSaltando){
            velocidadVertical = fuerzaSalto

            estaSaltando = true
        }
        else {
            print("ya esta saltando")
        }
    }

    private fun aplicarGravedad(){
        if (estaSaltando){
            velocidadVertical += gravedad
            img.y= velocidadVertical


            //verifica si toco el suelo
            if (img.y >= posicionYSuelo){
                img.y = posicionYSuelo
                velocidadVertical = 0f
                estaSaltando = false
            }
        }
    }




    private fun generarObstaculo(){
        val obstaculoVista = ImageView(this)

        val params = ConstraintLayout.LayoutParams(250,200)
        obstaculoVista.layoutParams = params

        val displayMetrics = resources.displayMetrics
        obstaculoVista.x = displayMetrics.widthPixels.toFloat()

        obstaculosGenerados++
        if(obstaculosGenerados % 10 == 0 && velocidadScrollActual < velocidadScrollMaxima){
            velocidadScrollActual += incrementoVelocidad
        }

        val random = (0..100).random()

        val tipoElegido = when {
            random < 35 -> TipoObstaculo.fruta
            random < 65 -> TipoObstaculo.verdura
            random < 85 -> TipoObstaculo.dulce
            else -> TipoObstaculo.arbusto
        }

        val posicionesY = when (tipoElegido){
            TipoObstaculo.arbusto -> {
                posicionYSuelo
            }
            else -> {
                val posicionesDisponibles = listOf(450f,600f, posicionYSuelo)
                posicionesDisponibles.random()
            }
        }

        obstaculoVista.y = posicionesY

        val puntos = when (tipoElegido) {

            TipoObstaculo.fruta -> {
                val frutas = listOf(
                    R.drawable.manzana,
                    R.drawable.naranja,
                    R.drawable.fresa )
                obstaculoVista.setImageResource(frutas.random())
                1
            }

            TipoObstaculo.verdura ->{
                val verduras = listOf(
                    R.drawable.brocoli,
                    R.drawable.zanahoria,
                    R.drawable.calabaza
                                     )
                obstaculoVista.setImageResource(verduras.random())
                2
            }

            TipoObstaculo.dulce -> {
                val dulces = listOf(
                    R.drawable.paleta,
                    R.drawable.caramelo,
                    R.drawable.chocolate )
                obstaculoVista.setImageResource(dulces.random())
                -1
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
            obstaculo.vista.x -= velocidadScrollActual

            if(obstaculo.vista.x + obstaculo.vista.width < 0){
                if (obstaculo.tipo == TipoObstaculo.arbusto) {
                    obstaculosEvitados++
                }
                obstaculosAEliminar.add(obstaculo)
            }
        }

        for (obstaculo in obstaculosAEliminar){
            layout.removeView(obstaculo.vista)
            obstaculos.remove(obstaculo)
        }

    }

    private fun hayColision(vista1: ImageView, vista2: ImageView): Boolean {
         val x1 = vista1.x
         val y1 = vista1.y
         val ancho1 = vista1.width
         val alto1 = vista1.height

         val x2 = vista2.x
         val y2 = vista2.y
         val ancho2 = vista2.width
         val alto2 = vista2.height

        return x1 < x2 + ancho2 && x1 + ancho1 > x2 &&
                y1 < y2 + alto2 && y1 + alto1 > y2
    }

    private fun verificarColisiones(){

        val obstaculosColisionados = mutableListOf<Obstaculo>()
         for(obstaculo in obstaculos){
             if (hayColision(img,obstaculo.vista)){
                 obstaculosColisionados.add(obstaculo)
             }
         }

        for (obstaculo in obstaculosColisionados){
            manejarColision(obstaculo)
        }

    }

    private fun manejarColision(obstaculo: Obstaculo){

        when (obstaculo.tipo){
            TipoObstaculo.fruta -> {
                frutasComidas++
                puntuacion += obstaculo.puntos
                print("¡Fruta! +${obstaculo.puntos} puntos. Total: $puntuacion")

                actualizarPuntuacion()

                layout.removeView(obstaculo.vista)
                obstaculos.remove(obstaculo)
            }

            TipoObstaculo.verdura -> {
                verdurasComidas++
                puntuacion += obstaculo.puntos
                print("¡Verdura! +${obstaculo.puntos} puntos. Total: $puntuacion")

                actualizarPuntuacion()

                layout.removeView(obstaculo.vista)
                obstaculos.remove(obstaculo)
            }
            TipoObstaculo.dulce -> {
                dulcesComidos++
                puntuacion += obstaculo.puntos


                actualizarPuntuacion()

                if (puntuacion < 0){
                    puntuacion = 0
                    actualizarPuntuacion()
                    detenerJuego()
                    ocultarPanel()

                    temporizadorActivo = false

                    perderVida()

                    if (vidasActuales > 0) {
                        mostrarDialogoReintentar()
                    } else {
                        mostrarDialogoGameOver()
                    }
                    return

                }

                layout.removeView(obstaculo.vista)
                obstaculos.remove(obstaculo)
            }
            TipoObstaculo.arbusto -> {
                layout.removeView(obstaculo.vista)
                obstaculos.remove(obstaculo)

                detenerJuego()
                ocultarPanel()

                temporizadorActivo = false

                perderVida()

                if (vidasActuales > 0){
                    mostrarDialogoReintentar()
                }
                else {
                    mostrarDialogoGameOver()
                }



            }
        }

    }

    private fun mostrarDialogoReintentar(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialogo_reintentar)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val TVReintentar = dialog.findViewById<ImageView>(R.id.TVReintentar)
        val TVSalir = dialog.findViewById<ImageView>(R.id.TVSalir)
        val txtVidasRestantes = dialog.findViewById<TextView>(R.id.txtVidasRestantes)
        val txtPuntuacionActual = dialog.findViewById<TextView>(R.id.txtPuntuacionActual)

        txtVidasRestantes?.text = "Vidas restantes: $vidasActuales"
        txtPuntuacionActual?.text = "Puntuación: $puntuacion"

        TVReintentar?.setOnClickListener {
            dialog.dismiss()
            temporizadorActivo = true
            reiniciarRonda()
        }


        TVSalir?.setOnClickListener {
            dialog.dismiss()
            temporizadorActivo = false
            abandonarPartida()
        }

        dialog.show()

    }
    private fun mostrarDialogoGameOver() {

        temporizadorActivo = false

        try {

            val idJugador = nombreUsuario
            gameDataManager.guardarPartida(
                idJugador = idJugador,
                alias = nombreUsuario,
                animal = animalNombre,
                tiempo = tiempoDeJuego,
                puntos = puntuacion,
                frutas = frutasComidas,
                verduras = verdurasComidas,
                dulces = dulcesComidos,
                obstaculos = obstaculosEvitados,
                vidas = vidasMaximas - vidasActuales)

            val ruta = gameDataManager.getPartidasPath()
            android.util.Log.d("Juego", "Partida guardada en carpeta: $ruta")
        } catch (e: Exception) {
            android.util.Log.e("Juego", "Error guardando partida: ${e.message}")
        }

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialogo_game_over)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnSalir = dialog.findViewById<Button>(R.id.btnSalirGameOver)
        val txtPuntuacionFinal = dialog.findViewById<TextView>(R.id.txtPuntuacionFinal)

        txtPuntuacionFinal?.text = "Puntuación final: $puntuacion"

        btnSalir?.setOnClickListener {
            dialog.dismiss()
            volverAlInicio()
        }

        dialog.show()
    }

    private fun reiniciarRonda() {
        for (obstaculo in obstaculos) {
            layout.removeView(obstaculo.vista)
        }
        obstaculos.clear()

        // Resetear la posición del animal
        img.y = posicionYSuelo

        velocidadVertical = 0f
        estaSaltando = false

        mostrarPanel()
        temporizadorActivo = true
        iniciarJuego()

    }
    private fun abandonarPartida() {
        temporizadorActivo  = false

//        try {
//            val idJugador = nombreUsuario
//            gameDataManager.guardarPartida(
//                idJugador = idJugador,
//                alias = nombreUsuario,
//                animal = animalNombre,
//                tiempo = tiempoDeJuego,
//                puntos = puntuacion,
//                frutas = frutasComidas,
//                verduras = verdurasComidas,
//                dulces = dulcesComidos,
//                obstaculos = obstaculosEvitados,
//                vidas = vidasMaximas - vidasActuales
//            )
//            Log.d("Juego", "Partida guardada al abandonar")
//        } catch (e: Exception) {
//            Log.e("Juego", "Error guardando partida: ${e.message}")
//        }

        puntuacion = 0
        vidasActuales = vidasMaximas

        for (obstaculo in obstaculos) {
            layout.removeView(obstaculo.vista)
        }
        obstaculos.clear()

        velocidadVertical = 0f
        estaSaltando = false

        volverAlInicio()
    }

    private fun volverAlInicio() {
        val intent = Intent(this, Inicio::class.java)
        startActivity(intent)
        finish()
    }

    private fun actualizarVidas(){
        val imagenVidas = when(vidasActuales){
            3 -> R.drawable.vida_3
            2 -> R.drawable.vida_2
            1 -> R.drawable.vida_1
            0 -> R.drawable.vidas_0
            else -> R.drawable.vidas_0
        }

        imgVidas.setImageResource(imagenVidas)
    }

    private fun perderVida(){
        vidasActuales--

        actualizarVidas()

    }

    private fun gameOver(){
        detenerJuego()
        ocultarPanel()
        mostrarDialogoGameOver()
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
        detenerJuego()

        for(obstaculo in obstaculos){
            layout.removeView(obstaculo.vista)
        }
        obstaculos.clear()
    }

//    private fun mostrarDialogoReiniciar(){
//        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(false)
//        dialog.setContentView(R.layout.dialogo_reiniciar)
//        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//
//        val IvReiniciar = dialog.findViewById<ImageView>(R.id.iVRegresar)
//        val IvSalirJuego = dialog.findViewById<ImageView>(R.id.iVSalir)
//
//        val textoPuntuacion = dialog.findViewById<TextView>(R.id.textoPuntuacion)
//        textoPuntuacion?.text = "Puntuacion: $puntuacion"
//
//        IvReiniciar.setOnClickListener {
//            dialog.dismiss()
//            reiniciarJuego()
//        }
//
//        IvSalirJuego.setOnClickListener {
//            dialog.dismiss()
//            val intent = Intent(this, Inicio::class.java)
//            startActivity(intent)
//            finish()
//        }
//
//        dialog.show()
//
//    }

    fun mostrarSalirJuego(){

        detenerJuego()
        ocultarPanel()

        temporizadorActivo = false


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
            puntuacion = 0
            volverAlInicio()
        }

        btnSalirNOJ?.setOnClickListener {
            dialog.dismiss()

            mostrarPanel()
            temporizadorActivo = true
            iniciarJuego()
        }

        dialog.show()
    }

    private fun actualizarPuntuacion(){
        txtPuntuacion.text = "Puntos\n$puntuacion"
    }

    private fun mostrarPanel(){
        panelSuperior.visibility = View.VISIBLE
    }

    private fun ocultarPanel(){
        panelSuperior.visibility = View.GONE
    }

    private fun iniciarTemporizador() {
        temporizadorActivo = true
        val runnable = object : Runnable {
            override fun run() {
                if(temporizadorActivo) {
                    tiempoDeJuego++
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(runnable)
    }

    private fun reiniciarContadores() {
        frutasComidas = 0
        verdurasComidas = 0
        dulcesComidos = 0
        obstaculosEvitados = 0
        tiempoDeJuego = 0
    }
}

