package com.tuapp.juego.managers

import android.content.Context
import android.os.Build
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class GameDataManager(private val context: Context) {

    companion object {
        private const val FILE_NAME = "game_data.json"
        private const val VERSION = "1.0"
    }

    private val dataFile: File = File(context.getExternalFilesDir(null), FILE_NAME)

    init {
        inicializarArchivoSiNoExiste()
    }

    private fun inicializarArchivoSiNoExiste() {
        if (!dataFile.exists()) {
            val root = JSONObject().apply {
                put("version", VERSION)
                put("jugadores", JSONArray())
                put("partidas", JSONArray())
                put("animales_disposibles", JSONArray().apply {
                    put("gato")
                    put("zorro")
                    put("dinosaurio")
                    put("koala")
                    put("conejo")
                })
            }
        }
    }

    fun crearNuevoJugador(nombre: String, alias: String, animalFavorito: String): String? {
        return try {
            val idJugador = UUID.randomUUID().toString()
            val root = leerJSON()
            val jugadores = root.getJSONArray("jugadores")

            val nuevoJugador = JSONObject().apply {
                put("id_jugador", idJugador)
                put("nombre", nombre)
                put("alias", alias)
                put("animal_favorito", animalFavorito)
                put("fecha_primer_juego", obtenerFechaISO())
                put("total_partidas", 0)
                put("mejor_puntuacion", 0)
            }

            jugadores.put(nuevoJugador)
            escribirJSON(root)
            idJugador
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun actualizarEstadisticasJugador(root: JSONObject, idJugador: String, puntos: Int) {

        val jugadores = root.getJSONArray("jugadores")
        for (i in 0 until jugadores.length()) {
            val jugador = jugadores.getJSONObject(i)
            if (jugador.getString("id_jugador") == idJugador) {
                jugador.put("total_partidas", jugador.getInt("total_partidas") + 1)
                val mejor = jugador.getInt("mejor_puntuación")
                if (puntos > mejor) jugador.put("mejor_puntuacion", puntos)
                break
            }
        }
    }

    fun guardarPartida(
        idJugador: String,
        puntosTotales: Int,
        frutasComidas: Int,
        verdurasComidas: Int,
        dulcesComidos: Int,
        obstaculosChocados: Int,
        tiempoSegundos: Int
                      ): Boolean {
        return try {
            val root = leerJSON()
            actualizarEstadisticasJugador(root, idJugador, puntosTotales)

            val partidas = root.getJSONArray("partidas")
            val partida = JSONObject().apply {
                put("id_partida", UUID.randomUUID().toString())
                put("id_jugador", idJugador)
                put("fecha_hora", obtenerFechaISO())
                put("puntos_totales", puntosTotales)
                put("frutas_comidas", frutasComidas)
                put("verduras_comidas", verdurasComidas)
                put("dulces_comidos", dulcesComidos)
                put("obstaculos_chocados", obstaculosChocados)
                put("tiempo_juego_segundos", tiempoSegundos)
                put("detalles", JSONObject().apply {
                    put("puntos_frutas", frutasComidas * 2)
                    put("puntos_verduras", verdurasComidas * 5)
                    put("puntos_perdidos_dulces", dulcesComidos * -1)
                })
            }
            partidas.put(partida)
            escribirJSON(root)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    data class RankingEntry(
        val alias: String, val animal: String, val mejorPuntuacion: Int)

    fun obtenerRanking(limite: Int = 10): List<RankingEntry> {
        return try {
            val jugadores = leerJSON().getJSONArray("jugadores")
            val lista = mutableListOf<RankingEntry>()
            for (i in 0 until jugadores.length()) {
                val jugador = jugadores.getJSONObject(i)
                lista.add(
                    RankingEntry(
                        alias = jugador.getString("alias"),
                        animal = jugador.getString("animal_favorito"),
                        mejorPuntuacion = jugador.getInt("mejor_puntuacion")
                                )
                         )
            }
            lista.sortedByDescending { it.mejorPuntuacion }.take(limite)

    } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
    }
}



    private fun leerJSON(): JSONObject = JSONObject(dataFile.readText())
    private fun escribirJSON(root: JSONObject) = dataFile.writeText(root.toString(2))
    private fun obtenerFechaISO(): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(
        Date())
    fun archivoExiste(): Boolean = dataFile.exists()
    fun getRutaArchivo(): String = dataFile.absolutePath
    fun mostrarJSONEnlog() {
        try {
            Log.d("GameData", leerJSON().toString(2))
        }catch (e: Exception) {
            Log.e("GameData", "Error leyendo JSON", e)
        }
    }
}