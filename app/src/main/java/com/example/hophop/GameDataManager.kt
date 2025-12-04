package com.example.hophop

import android.content.Context
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

    fun getPartidasPath(): String {
        return getPartidasDirectory().absolutePath
    }

    fun leerTodasLaspartidas(): List<Partida>{
        val partidas = mutableListOf<Partida>()
        val dir = getPartidasDirectory()

        try{
            val archivos = dir.listFiles{ file -> file.extension == "json"}

            archivos?.forEach { file ->
                try{
                    val jsonString = file.readText()
                    val json = JSONObject(jsonString)

                    val partida = Partida(
                        idPartida = json.getString("id_partida"),
                        NombreJugador = json.getString("id_jugador"),
                        alias = json.getString("alias"),
                        animal = json.getString("animal"),
                        fechaHora = json.getString("fecha_hora"),
                        tiempoJuegoSegundos = json.getInt("tiempo_juego_segundos"),
                        puntosTotales = json.getInt("puntos_totales"),
                        frutasComidas = json.getInt("frutas_comidas"),
                        verdurasComidas = json.getInt("verduras_comidas"),
                        dulcesComidos = json.getInt("dulces_comidos"),
                        ObstaculosEvitados = json.getInt("obstaculos_evitados"),
                        VidasPerdidas = json.getInt("vidas_perdidas")
                                         )
                    partidas.add(partida)
                } catch (e: Exception){
                    Log.e("GameDataManager", "Error leyendo archivo ${file.name}: ${e.message}")
                }

            }
            Log.d("GameDataManager", "Total partidas leídas: ${partidas.size}")
        }
        catch (e: Exception){
            Log.e("GameDataManager", "Error leyendo directorio: ${e.message}")

        }

        return partidas
    }


    fun obtenerPartidasComoJSONArray (): String {
        val partidas = leerTodasLaspartidas()
        val jsonArray = JSONArray()

        partidas.forEach { partida ->
            val json = JSONObject().apply {
                put("id_partida", partida.idPartida)
                put("id_jugador",partida.NombreJugador)
                put("alias", partida.alias)
                put("animal",partida.animal)
                put("fecha_hora",partida.fechaHora)
                put("tiempo_juego_segundos",partida.tiempoJuegoSegundos)
                put("puntos_totales",partida.puntosTotales)
                put("frutas_comidas",partida.frutasComidas)
                put("verduras_comidas", partida.verdurasComidas)
                put("dulces_comidos", partida.dulcesComidos)
                put("obstaculos_evitados", partida.ObstaculosEvitados)
                put("vidas_perdidas", partida.VidasPerdidas)
            }
            jsonArray.put(json)
        }
        return jsonArray.toString()
    }

    fun contarPartidas(): Int {
        return getPartidasDirectory().listFiles() { file -> file.extension == "json"}
            ?.size ?: 0

    }
    fun obtenerUltimasPartidas(limite: Int = 10 ): List<Partida>{
        return leerTodasLaspartidas().sortedByDescending { it.fechaHora }.take(limite)
    }

    fun eliminarTodasLasPartidas(){

        try {
            val dir = getPartidasDirectory()
            dir.listFiles()?.forEach { file ->
                file.delete()
            }
            Log.d("GameDataManager","Todas las partidas eliminadas")

        } catch (e: Exception){
            Log.e("GameDataManager", "Error eliminado partidas: ${e.message}")

        }

    }

    fun tieneDatosInsuficientes(minimo: Int = 5): Boolean {
        val total = contarPartidas()

        Log.d("GameDataManager","Partidas Disponibles: $total (minimo: $minimo)")

        return total >= minimo

    }

}