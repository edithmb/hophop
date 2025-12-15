package com.example.hophop

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.util.Log
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
    private var anchoFondo = 0
    private val obstaculos = mutableListOf<Obstaculo>()

    private val listaAlturasVuelo = listOf(-400f, -250f)
    private val listaFrutas = listOf(R.drawable.manzana, R.drawable.naranja, R.drawable.fresa)
    private val listaVerduras = listOf(R.drawable.brocoli, R.drawable.zanahoria, R.drawable.calabaza)
    private val listaDulces = listOf(R.drawable.paleta, R.drawable.caramelo, R.drawable.chocolate)
    private val handlerObstaculos = Handler(Looper.getMainLooper())
    private var juegoActivo = false
    private var estaMoviendo = false
    private var puntuacion = 0
    private val listaColisionesTemp = ArrayList<Obstaculo>()
    //variables de salto
    private var velocidadVertical: Float = 0f
    private val gravedad: Float = 1.1f
    private val fuerzaSalto: Float =-45f
    private var posicionYSuelo: Float = 0f
    private var altoPantalla: Int = 0
    private var anchoPantalla: Int = 0
    private var estaSaltando: Boolean = false
    //variables de velocidad
    private var velocidadScrollActual = 8f
    private val velocidadScrollMaxima = 100f
    private val incrementoVelocidad = 0.5f
    private var obstaculosGenerados = 0
    private val tiempoGeneracionObstaculos = 3000L
    private val segundosParaAumentarVelocidad = 5
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

    private val temporizadorRunnable = object: Runnable {
        override fun run() {
            if (temporizadorActivo){
                tiempoDeJuego++

                if (tiempoDeJuego > 0 && tiempoDeJuego % segundosParaAumentarVelocidad == 0) {
                    // Aumenta la velocidad si no ha alcanzado el máximo
                    if (velocidadScrollActual < velocidadScrollMaxima) {
                        velocidadScrollActual += incrementoVelocidad
                        // Opcional: para ver en la consola cómo aumenta
                        Log.d("Juego", "Velocidad aumentada a: $velocidadScrollActual")
                    }
                }


                handler.postDelayed(this, 1000)
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
                verificarColisiones()

                handler.postDelayed(this,16)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)

        val displayMetrics = resources.displayMetrics
        altoPantalla = displayMetrics.heightPixels
        anchoPantalla = displayMetrics.widthPixels

        posicionYSuelo = altoPantalla * 0.75f

        gameDataManager = GameDataManager(this)

        layout = findViewById(R.id.juegolayout)

        panelSuperior = findViewById(R.id.panelSuperior)
        txtPuntuacion = findViewById(R.id.txtPuntuacion)
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario)
        imgVidas = findViewById(R.id.imgVidas)

        inicializarObstaculos()

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
    private fun iniciarJuego(){

        handlerObstaculos.removeCallbacks(generadorObstaculos)
        handler.removeCallbacks(movimientoRunnable)

        animalAnimation.start()
        estaMoviendo = true
        handler.post(movimientoRunnable)

        juegoActivo = true
        handlerObstaculos.post(generadorObstaculos)

    }

    private fun inicializarObstaculos(){

        // crea 10 obstaculos y los deja "escondidos"
        for (i in 0 until 10){
            val obstaculoVista = ImageView(this)
            val params = ConstraintLayout.LayoutParams(250,200)
            obstaculoVista.layoutParams = params

           //fuera de la pantalla
            obstaculoVista.x = -500f
            obstaculoVista.y = posicionYSuelo
            obstaculoVista.visibility = View.INVISIBLE
            layout.addView(obstaculoVista)


            //objeto de datos desactivado
            val nuevoObstaculo = Obstaculo(
                vista = obstaculoVista,
                tipo = TipoObstaculo.arbusto,
                puntos = 0,
                activo = false
            )

            obstaculoVista.translationZ = 10f
            
            obstaculos.add(nuevoObstaculo)


        }



    }

    private fun reiniciarJuego(){
        detenerJuego()

        for(obstaculo in obstaculos){
            obstaculo.activo = false
            obstaculo.vista.visibility = View.INVISIBLE
            obstaculo.vista.x = -1000f
        }


        puntuacion = 0

        velocidadScrollActual = 8f
        obstaculosGenerados = 0
        velocidadVertical = 0f
        estaSaltando = false

        actualizarPuntuacion()
        mostrarPanel()
        img.y = posicionYSuelo

        iniciarJuego()
    }

    private fun detenerJuego(){
        juegoActivo = false
        estaMoviendo = false
        temporizadorActivo = false

        if(animalAnimation.isRunning){
            animalAnimation.stop()
        }

        handler.removeCallbacks(movimientoRunnable)
        handler.removeCallbacks(temporizadorRunnable)
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
            img.y += velocidadVertical


            //verifica si toco el suelo
            if (img.y >= posicionYSuelo){
                img.y = posicionYSuelo
                velocidadVertical = 0f
                estaSaltando = false
            }
        }
    }
    private fun generarObstaculo(){
        //buscar el primer libre
        val obstaculo = obstaculos.firstOrNull { !it.activo }
        if (obstaculo == null) return

        obstaculo.vista.x = anchoPantalla.toFloat()
        obstaculo.vista.visibility = View.VISIBLE
        obstaculo.activo = true

        obstaculosGenerados++

        if (obstaculosGenerados % 15 == 0 && velocidadScrollActual < velocidadScrollMaxima) {
            velocidadScrollActual += incrementoVelocidad
        }

        val random = kotlin.random.Random.nextInt(0, 101)


        val tipoElegido = when {
            random < 35 -> TipoObstaculo.fruta
            random < 65 -> TipoObstaculo.verdura
            random < 85 -> TipoObstaculo.dulce
            else -> TipoObstaculo.arbusto
        }
        obstaculo.tipo = tipoElegido

        val posicionesY = when (tipoElegido){
            TipoObstaculo.arbusto -> posicionYSuelo
            else ->(posicionYSuelo + listaAlturasVuelo.random())

        }

        obstaculo.vista.y = posicionesY

        val puntos = when (tipoElegido) {

            TipoObstaculo.fruta -> { obstaculo.vista.setImageResource(listaFrutas.random())
                1
            }

            TipoObstaculo.verdura ->{ obstaculo.vista.setImageResource(listaVerduras.random())
                2
            }

            TipoObstaculo.dulce -> { obstaculo.vista.setImageResource(listaDulces.random())
                -1
            }

            TipoObstaculo.arbusto -> {
                obstaculo.vista.setImageResource(R.drawable.arbusto)
                0
            }
        }
        obstaculo.puntos = puntos
    }
    private fun moverObstaculos(){

        for (obstaculo in obstaculos){
            // solo se mueven los activos
            if (obstaculo.activo){
                obstaculo.vista.x -= velocidadScrollActual

                if(obstaculo.vista.x + obstaculo.vista.width < 0){

                    if(obstaculo.tipo == TipoObstaculo.arbusto){
                        obstaculosEvitados++
                    }

                    // desactivar y mandar lejos
                    obstaculo.activo = false
                    obstaculo.vista.visibility = View.INVISIBLE
                    obstaculo.vista.x = -1000f
                }
            }
        }
    }
    private fun hayColision(vista1: ImageView, vista2: ImageView): Boolean {
        //para que alcance a saltar
        val margen = 0.15f

        val x1 = vista1.x + (vista1.width * margen)
        val y1 = vista1.y + (vista1.height * margen)
        val ancho1 = vista1.width * (1 - 2 * margen)
        val alto1 = vista1.height * (1 - 2 * margen)

        val x2 = vista2.x + (vista2.width * margen)
        val y2 = vista2.y + (vista2.height * margen)
        val ancho2 = vista2.width * (1 - 2 * margen)
        val alto2 = vista2.height * (1 - 2 * margen)

        return x1 < x2 + ancho2 && x1 + ancho1 > x2 &&
                y1 < y2 + alto2 && y1 + alto1 > y2
    }

    private fun verificarColisiones() {

        for (obstaculo in obstaculos) {

            if (obstaculo.activo && obstaculo.vista.x < (img.x + img.width) &&
                obstaculo.vista.x > (img.x - obstaculo.vista.width)) {
                if (hayColision(img, obstaculo.vista)) {
                    manejarColision(obstaculo)
                    if (!juegoActivo) break
                }
            }
        }
    }
    private fun manejarColision(obstaculo: Obstaculo){

        when (obstaculo.tipo){
            TipoObstaculo.fruta -> {
                frutasComidas++
                puntuacion += obstaculo.puntos
                print("¡Fruta! +${obstaculo.puntos} puntos. Total: $puntuacion")

                actualizarPuntuacion()

                obstaculo.activo = false
                obstaculo.vista.visibility = View.INVISIBLE
                obstaculo.vista.x = -1000f
            }

            TipoObstaculo.verdura -> {
                verdurasComidas++
                puntuacion += obstaculo.puntos
                print("¡Verdura! +${obstaculo.puntos} puntos. Total: $puntuacion")

                actualizarPuntuacion()

                obstaculo.activo = false
                obstaculo.vista.visibility = View.INVISIBLE
                obstaculo.vista.x = -1000f
            }
            TipoObstaculo.dulce -> {
                dulcesComidos++
                puntuacion += obstaculo.puntos


                actualizarPuntuacion()


                if (puntuacion < 0){
                    puntuacion = 0
                    actualizarPuntuacion()

                    obstaculo.activo = false
                    obstaculo.vista.visibility = View.INVISIBLE
                    obstaculo.vista.x = -1000f

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

                obstaculo.activo = false
                obstaculo.vista.visibility = View.INVISIBLE
                obstaculo.vista.x = -1000f
            }
            TipoObstaculo.arbusto -> {
                obstaculo.activo = false
                obstaculo.vista.visibility = View.INVISIBLE
                obstaculo.vista.x = -1000f


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
            vidasActuales = vidasMaximas
            reiniciarContadores()
            volverAlInicio()
        }
        dialog.show()
    }
    private fun reiniciarRonda() {
        for(obstaculo in obstaculos){
            obstaculo.activo = false
            obstaculo.vista.visibility = View.INVISIBLE
            obstaculo.vista.x = -1000f
        }
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
                vidas = vidasMaximas - vidasActuales
            )
            Log.d("Juego", "Partida guardada al abandonar")
        } catch (e: Exception) {
            Log.e("Juego", "Error guardando partida: ${e.message}")
        }
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

    }
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
        handler.post(temporizadorRunnable)
    }
    private fun reiniciarContadores() {
        frutasComidas = 0
        verdurasComidas = 0
        dulcesComidos = 0
        obstaculosEvitados = 0
        tiempoDeJuego = 0
    }
}