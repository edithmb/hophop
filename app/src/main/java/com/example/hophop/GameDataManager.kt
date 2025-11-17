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
    val adJugador: String,
    val alias: String,
    val animal: String,
    val fechaHora: String,
    val tiempoJuegoSegundos: Int,
    val puntosTotales: Int,
    val frutasComidas: Int,
    val verdurasComidas: Int,
    val dulcesComidos: Int,
    val ObstaculosChocados: Int )

class GameDataManager (private val context: Context) {

    private val fileName = "partidas.json"

    private fun loadJson(): JSONArray {
        return try {
            val file = context.getFileStreamPath(fileName)
            if (!file.exists()) {
                JSONArray()
            } else {
                val text = context.openFileInput(fileName).bufferedReader().use { it.readText() }
                JSONArray(text)
            }
        } catch (e: Exception) {
            JSONArray()
        }
    }

    private fun saveJson(jsonArray: JSONArray) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(jsonArray.toString().toByteArray()) }
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
        obstaculos:Int) {


        val json = loadJson()

        val partidaJson = JSONObject().apply {
            put("id_partida", UUID.randomUUID().toString())
            put("id_jugador", idJugador)
            put("alias", alias)
            put("animal", animal)

            val fecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            put("fecha_hora", fecha)

            put("tiempo_juego_segundos", tiempo)
            put("puntos_totales", puntos)
            put("frutas_comidas", frutas)
            put("verduras_comidas", verduras)
            put("dulces_comidos", dulces)
            put("obstaculos_chocados", obstaculos)
        }

        json.put(partidaJson)
        saveJson(json)
    }
}