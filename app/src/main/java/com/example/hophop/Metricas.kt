package com.example.hophop

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MetricasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.metricas)

        val btnVolver = findViewById<Button>(R.id.btnVolverMetricas)

        btnVolver.setOnClickListener {
            finish()
        }
    }
}