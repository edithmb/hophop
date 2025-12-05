package com.example.hophop

import android.widget.ImageView

data class Obstaculo (
    val vista: ImageView,
    var tipo: TipoObstaculo,
    var puntos: Int = 0,
    var activo: Boolean = false
                     )

enum class TipoObstaculo{
    fruta,
    verdura,
    dulce,
    arbusto
}