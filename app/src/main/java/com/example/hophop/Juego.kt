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
import android.widget.FrameLayout // IMPORTANTE
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

    // VARIABLES GRÁFICAS
    private lateinit var img: ImageView
    private lateinit var animalAnimation: AnimationDrawable
    private lateinit var fondo1: ImageView
    private lateinit var fondo2: ImageView

    //FrameLayout como contenedor rápido
    private lateinit var contenedorJuego: FrameLayout

    private val handler = Handler(Looper.getMainLooper())
    private var anchoFondo = 0
    private val obstaculos = mutableListOf<Obstaculo>()
    private val handlerObstaculos = Handler(Looper.getMainLooper())
    private var juegoActivo = false
    private var estaMoviendo = false
    private var puntuacion = 0

    // Variables de física
    private var velocidadVertical: Float = 0f
    private val gravedad: Float = 1.1f
    private val fuerzaSalto: Float =-45f
    private var posicionYSuelo: Float = 0f
    private var altoPantalla: Int = 0
    private var anchoPantalla: Int = 0
    private var estaSaltando: Boolean = false

    // Variables de velocidad
    private var velocidadScrollActual = 8f
    private val velocidadScrollMaxima = 100f
    private val incrementoVelocidad = 0.5f
    private var obstaculosGenerados = 0
    private val tiempoGeneracionObstaculos = 3000L
    private val segundosParaAumentarVelocidad = 5

    // Datos
    private lateinit var gameDataManager: GameDataManager
    private var animalNombre: String = "Desconocido"
    private var frutasComidas = 0
    private var verdurasComidas = 0
    private var dulcesComidos = 0
    private var obstaculosEvitados = 0
    private var tiempoDeJuego = 0
    private var temporizadorActivo = false

    //Listas precargadas para no saturar la memoria
    private val listaFrutas = listOf(R.drawable.manzana, R.drawable.naranja, R.drawable.fresa)
    private val listaVerduras = listOf(R.drawable.brocoli, R.drawable.zanahoria, R.drawable.calabaza)
    private val listaDulces = listOf(R.drawable.paleta, R.drawable.caramelo, R.drawable.chocolate)
    private val alturasVuelo = listOf(400f, 250f)

    private var generadorObstaculos = object : Runnable {
        override fun run() {
            if (juegoActivo){
                generarObstaculo()
                handlerObstaculos.postDelayed(this, tiempoGeneracionObstaculos)
            }
        }
    }

    //Bucle unificado (Movimiento + Colisiones + Gravedad)
    private var movimientoRunnable = object : Runnable {
        override fun run() {
            if (estaMoviendo){
                //Mover Fondo
                fondo1.x -= velocidadScrollActual
                fondo2.x -= velocidadScrollActual

                if (fondo1.x + anchoFondo <= 0) fondo1.x = fondo2.x + anchoFondo
                if (fondo2.x + anchoFondo <= 0) fondo2.x = fondo1.x + anchoFondo

                //Mover Obstáculos y Gravedad
                moverObstaculos()
                aplicarGravedad()

                //Verificar Colisiones
                verificarColisiones()

                handler.postDelayed(this, 16) // ~60 FPS
            }
        }
    }

    private val temporizadorRunnable = object: Runnable {
        override fun run() {
            if (temporizadorActivo){
                tiempoDeJuego++
                if (tiempoDeJuego > 0 && tiempoDeJuego % segundosParaAumentarVelocidad == 0) {
                    if (velocidadScrollActual < velocidadScrollMaxima) {
                        velocidadScrollActual += incrementoVelocidad
                    }
                }
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juego)

        // Inicializar contenedor optimizado
        contenedorJuego = findViewById(R.id.contenedorJuego)

        val displayMetrics = resources.displayMetrics
        altoPantalla = displayMetrics.heightPixels
        anchoPantalla = displayMetrics.widthPixels
        posicionYSuelo = altoPantalla * 0.75f

        gameDataManager = GameDataManager(this)

        panelSuperior = findViewById(R.id.panelSuperior)
        txtPuntuacion = findViewById(R.id.txtPuntuacion)
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario)
        imgVidas = findViewById(R.id.imgVidas)

        // Fondo y obstáculos se añaden al FrameLayout (contenedorJuego)
        moverFondo()
        inicializarObstaculos()

        vidasActuales = vidasMaximas
        actualizarVidas()

        var sharedPref = getSharedPreferences("DatosJugador", Context.MODE_PRIVATE)
        nombreUsuario = sharedPref.getString("apodoJugador", "Jugador") ?: "Jugador"
        txtNombreUsuario.text = nombreUsuario

        val animalSeleccionado = intent.getIntExtra("animalSeleccionado", -1)
        val btnregresar = findViewById<Button>(R.id.btnRegresar)
        val txtviewprofileimage = findViewById<ImageView>(R.id.profile_image)

        // Configurar Jugador
        img = ImageView(this)
        val params = FrameLayout.LayoutParams(300, 300)
        img.y = posicionYSuelo
        img.layoutParams = params
        img.translationZ = 20f // Capa superior

        when (animalSeleccionado){
            R.id.tarjetaConejo -> { img.setBackgroundResource(R.drawable.conejo_animation); animalNombre = "conejo" }
            R.id.tarjetaKoala -> { img.setBackgroundResource(R.drawable.koala_animation); animalNombre = "Koala" }
            R.id.tarjetaZorro -> { img.setBackgroundResource(R.drawable.zorro_animation); animalNombre = "Zorro" }
            R.id.tarjetaGato -> { img.setBackgroundResource(R.drawable.gato_animation); animalNombre = "Gato" }
            R.id.tarjetaDinosaurio -> { img.setBackgroundResource(R.drawable.dinosaurio_animation); animalNombre = "Dinosaurio" }
        }

        animalAnimation = img.background as AnimationDrawable
        contenedorJuego.addView(img)

        fondo1.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        fondo2.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        img.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        for (obstaculo in obstaculos) {
            obstaculo.vista.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }

        // Click en cualquier parte del FrameLayout salta
        contenedorJuego.setOnClickListener {
            if (!animalAnimation.isRunning) {
                reiniciarContadores()
                iniciarTemporizador()
                iniciarJuego()
            } else {
                saltar()
            }
        }

        // Listener original por si acaso tocan fuera
        findViewById<View>(R.id.juegolayout).setOnClickListener {
            if (juegoActivo) saltar()
        }

        btnregresar.setOnClickListener { mostrarSalirJuego() }

        // Cargar icono perfil
        val resourceId = when (animalSeleccionado){
            R.id.tarjetaConejo -> R.drawable.conejo
            R.id.tarjetaKoala -> R.drawable.koala
            R.id.tarjetaZorro -> R.drawable.zorro
            R.id.tarjetaGato -> R.drawable.gato
            R.id.tarjetaDinosaurio-> R.drawable.dinosaurio
            else -> R.drawable.conejo
        }
        txtviewprofileimage.setImageResource(resourceId)
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
        for (i in 0 until 10){
            val obstaculoVista = ImageView(this)
            val params = FrameLayout.LayoutParams(250, 200)
            obstaculoVista.layoutParams = params

            obstaculoVista.x = -500f
            obstaculoVista.y = posicionYSuelo
            obstaculoVista.visibility = View.INVISIBLE

            contenedorJuego.addView(obstaculoVista)

            val nuevoObstaculo = Obstaculo(
                vista = obstaculoVista,
                tipo = TipoObstaculo.arbusto,
                puntos = 0,
                activo = false
            )
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
    }

    private fun aplicarGravedad(){
        if (estaSaltando){
            velocidadVertical += gravedad
            img.y += velocidadVertical
            if (img.y >= posicionYSuelo){
                img.y = posicionYSuelo
                velocidadVertical = 0f
                estaSaltando = false
            }
        }
    }

    //Generación sin crear basura (Garbage Collection)
    private fun generarObstaculo(){
        val obstaculo = obstaculos.firstOrNull { !it.activo } ?: return

        obstaculo.vista.x = anchoPantalla.toFloat()
        obstaculo.vista.visibility = View.VISIBLE
        obstaculo.activo = true
        obstaculosGenerados++

        val random = kotlin.random.Random.nextInt(0, 101)

        val tipoElegido = when {
            random < 35 -> TipoObstaculo.fruta
            random < 65 -> TipoObstaculo.verdura
            random < 85 -> TipoObstaculo.dulce
            else -> TipoObstaculo.arbusto
        }
        obstaculo.tipo = tipoElegido

        // Z-Index y Escala por tipo
        if (tipoElegido == TipoObstaculo.arbusto) {
            obstaculo.vista.translationZ = 10f
            obstaculo.vista.scaleType = ImageView.ScaleType.FIT_XY
        } else {
            obstaculo.vista.translationZ = 15f
            obstaculo.vista.scaleType = ImageView.ScaleType.FIT_CENTER
        }

        val posicionY = when (tipoElegido){
            TipoObstaculo.arbusto -> posicionYSuelo
            else -> posicionYSuelo - alturasVuelo.random()
        }
        obstaculo.vista.y = posicionY

        val puntos = when (tipoElegido) {
            TipoObstaculo.fruta -> {
                obstaculo.vista.setImageResource(listaFrutas.random())
                1
            }
            TipoObstaculo.verdura ->{
                obstaculo.vista.setImageResource(listaVerduras.random())
                2
            }
            TipoObstaculo.dulce -> {
                obstaculo.vista.setImageResource(listaDulces.random())
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
            if (obstaculo.activo){
                obstaculo.vista.x -= velocidadScrollActual

                if(obstaculo.vista.x + obstaculo.vista.width < 0){
                    if(obstaculo.tipo == TipoObstaculo.arbusto){
                        obstaculosEvitados++
                    }
                    obstaculo.activo = false
                    obstaculo.vista.visibility = View.INVISIBLE
                    obstaculo.vista.x = -1000f
                }
            }
        }
    }

    private fun hayColision(vista1: ImageView, vista2: ImageView): Boolean {
        val margen = 0.20f // Margen un poco mayor para ser más justo
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

    //Verificación eficiente
    private fun verificarColisiones(){
        // Recorremos buscando colisión
        for(obstaculo in obstaculos){
            // Solo comprobamos si está visible y CERCANO al jugador
            if (obstaculo.activo &&
                obstaculo.vista.x < (img.x + img.width + 50) &&
                obstaculo.vista.x > (img.x - obstaculo.vista.width - 50)) {

                if (hayColision(img, obstaculo.vista)){
                    manejarColision(obstaculo)
                    // Si el juego acaba, paramos el bucle inmediatamente
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
                actualizarPuntuacion()
                desactivarObstaculo(obstaculo)
            }
            TipoObstaculo.verdura -> {
                verdurasComidas++
                puntuacion += obstaculo.puntos
                actualizarPuntuacion()
                desactivarObstaculo(obstaculo)
            }
            TipoObstaculo.dulce -> {
                dulcesComidos++
                puntuacion += obstaculo.puntos
                actualizarPuntuacion()
                desactivarObstaculo(obstaculo)

                if (puntuacion < 0){
                    puntuacion = 0
                    actualizarPuntuacion()
                    detenerJuego()
                    ocultarPanel()
                    temporizadorActivo = false
                    perderVida()
                    if (vidasActuales > 0) mostrarDialogoReintentar() else mostrarDialogoGameOver()
                }
            }
            TipoObstaculo.arbusto -> {
                desactivarObstaculo(obstaculo)
                detenerJuego()
                ocultarPanel()
                temporizadorActivo = false
                perderVida()
                if (vidasActuales > 0) mostrarDialogoReintentar() else mostrarDialogoGameOver()
            }
        }
    }

    private fun desactivarObstaculo(obstaculo: Obstaculo){
        obstaculo.activo = false
        obstaculo.vista.visibility = View.INVISIBLE
        obstaculo.vista.x = -1000f
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
        } catch (e: Exception) {
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
            gameDataManager.guardarPartida(
                idJugador = nombreUsuario, alias = nombreUsuario, animal = animalNombre,
                tiempo = tiempoDeJuego, puntos = puntuacion, frutas = frutasComidas,
                verduras = verdurasComidas, dulces = dulcesComidos,
                obstaculos = obstaculosEvitados, vidas = vidasMaximas - vidasActuales
            )
        } catch (e: Exception) { }
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
        // 🚀 FrameLayout params para el fondo
        val paramsFondo1 = FrameLayout.LayoutParams(anchoFondo, FrameLayout.LayoutParams.MATCH_PARENT)
        fondo1.layoutParams = paramsFondo1
        fondo1.setImageResource(R.drawable.fondo_juego1)
        fondo1.scaleType = ImageView.ScaleType.FIT_XY
        fondo1.x = 0f

        fondo2 = ImageView(this)
        val paramsFondo2 = FrameLayout.LayoutParams(anchoFondo, FrameLayout.LayoutParams.MATCH_PARENT)
        fondo2.layoutParams = paramsFondo2
        fondo2.setImageResource(R.drawable.fondo_juego2)
        fondo2.scaleType = ImageView.ScaleType.FIT_XY
        fondo2.x = anchoFondo.toFloat()

        contenedorJuego.addView(fondo1)
        contenedorJuego.addView(fondo2)
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
    private fun mostrarPanel(){ panelSuperior.visibility = View.VISIBLE }
    private fun ocultarPanel(){ panelSuperior.visibility = View.GONE }
    private fun iniciarTemporizador() {
        temporizadorActivo = true
        handler.post(temporizadorRunnable)
    }
    private fun reiniciarContadores() {
        frutasComidas = 0; verdurasComidas = 0; dulcesComidos = 0; obstaculosEvitados = 0; tiempoDeJuego = 0
    }
}