package com.example.systeminfoapp

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtInfo = findViewById<TextView>(R.id.txtInfo)

        val info = """
            Marca: ${Build.BRAND}
            Modelo: ${Build.MODEL}
            Dispositivo: ${Build.DEVICE}
            Fabricante: ${Build.MANUFACTURER}
            Produto: ${Build.PRODUCT}
            Hardware: ${Build.HARDWARE}
            Versão Android: ${Build.VERSION.RELEASE}
            SDK: ${Build.VERSION.SDK_INT}
        """.trimIndent()

        txtInfo.text = info
    }
}
