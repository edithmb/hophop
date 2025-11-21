package com.tuapp.juego.managers

import android.content.Context
import android.os.Build
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class Partida (
    val idPartida: String,
    val NombreJugador: String,
    val alias: String,
    val animal: String,
    val fechaHora: String,
    val tiempoJuegoSegundos: Int,
    val puntosTotales: Int,
    val frutasComidas: Int,
    val verdurasComidas: Int,
    val dulcesComidos: Int,
    val ObstaculosEvitados: Int,
    val VidasPerdidas: Int)

class GameDataManager (private val context: Context) {

    private val folderName = "partidas"
    private fun getPartidasDirectory(): File {
        val dir = File(context.getExternalFilesDir(null), folderName)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun guardarPartida(
        idJugador: String,
        alias: String,
        animal: String,
        tiempo:Int,
        puntos: Int,
        frutas: Int,
        verduras:Int,
        dulces:Int,
        obstaculos:Int,
        vidas: Int) {

        try {

            val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val idPartida = UUID.randomUUID().toString()
            val partidaJson = JSONObject().apply {
                put("id_partida", idPartida)
                put("id_jugador", idJugador)
                put("alias", alias)
                put("animal", animal)
                put("fecha_hora", fecha)
                put("tiempo_juego_segundos", tiempo)
                put("puntos_totales", puntos)
                put("frutas_comidas", frutas)
                put("verduras_comidas", verduras)
                put("dulces_comidos", dulces)
                put("obstaculos_evitados", obstaculos)
                put("vidas_perdidas", vidas)
            }

            val dir = getPartidasDirectory()

            val fechaParaNombre = fecha.replace(":", "-").replace(" ","_")
            val fileName = "partida_${fechaParaNombre}_$idPartida.json"

            val file = File(dir, fileName)

            file.writeText(partidaJson.toString(4))
            Log.d("GameDataManager", "Partida guardada en: ${file.absolutePath}")

        } catch (e: Exception) {
            Log.e("GameDataManager", "Error guardando la partida: ${e.message}")
        }

    }
}