package com.example.weatherapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var etCidade: EditText
    private lateinit var btnVerTempo: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvCidade: TextView
    private lateinit var tvTemperatura: TextView
    private lateinit var tvDescricao: TextView
    private lateinit var tvHumidade: TextView
    private lateinit var tvVento: TextView
    private lateinit var tvErro: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCidade = findViewById(R.id.etCidade)
        btnVerTempo = findViewById(R.id.btnVerTempo)
        progressBar = findViewById(R.id.progressBar)
        tvCidade = findViewById(R.id.tvCidade)
        tvTemperatura = findViewById(R.id.tvTemperatura)
        tvDescricao = findViewById(R.id.tvDescricao)
        tvHumidade = findViewById(R.id.tvHumidade)
        tvVento = findViewById(R.id.tvVento)
        tvErro = findViewById(R.id.tvErro)

        btnVerTempo.setOnClickListener {
            val cidade = etCidade.text.toString().trim()

            if (cidade.isEmpty()) {
                Toast.makeText(this, "Escreve uma cidade", Toast.LENGTH_SHORT).show()
            } else {
                procurarCidade(cidade)
            }
        }
    }

    private fun procurarCidade(nomeCidade: String) {
        limparCampos()
        progressBar.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(this)
        val url = "https://geocoding-api.open-meteo.com/v1/search?name=$nomeCidade&count=1&language=pt&format=json"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                tratarRespostaCidade(response)
            },
            {
                progressBar.visibility = View.GONE
                tvErro.text = "Erro ao procurar a cidade."
            }
        )

        queue.add(request)
    }

    private fun tratarRespostaCidade(response: JSONObject) {
        if (!response.has("results")) {
            progressBar.visibility = View.GONE
            tvErro.text = "Cidade não encontrada."
            return
        }

        val results = response.getJSONArray("results")
        if (results.length() == 0) {
            progressBar.visibility = View.GONE
            tvErro.text = "Cidade não encontrada."
            return
        }

        val cidadeObj = results.getJSONObject(0)
        val nome = cidadeObj.getString("name")
        val pais = cidadeObj.optString("country", "")
        val latitude = cidadeObj.getDouble("latitude")
        val longitude = cidadeObj.getDouble("longitude")

        buscarTempo(nome, pais, latitude, longitude)
    }

    private fun buscarTempo(nome: String, pais: String, latitude: Double, longitude: Double) {
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,relative_humidity_2m,wind_speed_10m,weather_code&timezone=auto"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                progressBar.visibility = View.GONE
                mostrarTempo(nome, pais, response)
            },
            {
                progressBar.visibility = View.GONE
                tvErro.text = "Erro ao obter os dados do tempo."
            }
        )

        queue.add(request)
    }

    private fun mostrarTempo(nome: String, pais: String, response: JSONObject) {
        val current = response.getJSONObject("current")

        val temperatura = current.getDouble("temperature_2m")
        val humidade = current.getInt("relative_humidity_2m")
        val vento = current.getDouble("wind_speed_10m")
        val codigoTempo = current.getInt("weather_code")

        tvCidade.text = "$nome, $pais"
        tvTemperatura.text = "Temperatura: $temperatura °C"
        tvDescricao.text = "Estado do tempo: ${traduzirCodigoTempo(codigoTempo)}"
        tvHumidade.text = "Humidade: $humidade%"
        tvVento.text = "Vento: $vento km/h"
    }

    private fun traduzirCodigoTempo(codigo: Int): String {
        return when (codigo) {
            0 -> "Céu limpo"
            1, 2, 3 -> "Parcialmente nublado"
            45, 48 -> "Nevoeiro"
            51, 53, 55 -> "Chuvisco"
            61, 63, 65 -> "Chuva"
            66, 67 -> "Chuva gelada"
            71, 73, 75 -> "Neve"
            77 -> "Grãos de neve"
            80, 81, 82 -> "Aguaceiros"
            85, 86 -> "Aguaceiros de neve"
            95 -> "Trovoada"
            96, 99 -> "Trovoada com granizo"
            else -> "Desconhecido"
        }
    }

    private fun limparCampos() {
        tvCidade.text = ""
        tvTemperatura.text = ""
        tvDescricao.text = ""
        tvHumidade.text = ""
        tvVento.text = ""
        tvErro.text = ""
    }
}