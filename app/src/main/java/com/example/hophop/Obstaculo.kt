package com.example.hophop

import android.widget.ImageView

data class Obstaculo (
    val vista: ImageView,
    val tipo: TipoObstaculo,
    val puntos: Int = 0
                     )

enum class TipoObstaculo{
    fruta,
    dulce,
    arbusto
}