package cm.a15022.geminiapistarter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GeminiImageApp()
                }
            }
        }
    }
}

data class QuickPrompt(
    val title: String,
    val prompt: String
)

private val quickPrompts = listOf(
    QuickPrompt(
        title = "Receita",
        prompt = "Dá-me uma receita simples com base nesta imagem."
    ),
    QuickPrompt(
        title = "Nome criativo",
        prompt = "Sugere um nome criativo para o alimento ou objeto desta imagem."
    ),
    QuickPrompt(
        title = "Ingredientes",
        prompt = "Indica os ingredientes prováveis que aparecem nesta imagem."
    ),
    QuickPrompt(
        title = "Calorias",
        prompt = "Faz uma estimativa simples das calorias aproximadas deste alimento."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiImageApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var prompt by remember { mutableStateOf("Diz-me o que aparece nesta imagem.") }
    var answer by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri).use { inputStream ->
                    selectedBitmap = BitmapFactory.decodeStream(inputStream)
                }
                answer = ""
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = "Erro ao abrir imagem: ${e.message}"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gemini Image App") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Seleciona uma imagem, escolhe um prompt rápido ou escreve o teu próprio prompt e envia para o Gemini.",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    imagePickerLauncher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Escolher imagem")
            }

            Spacer(modifier = Modifier.height(16.dp))

            val bitmap = selectedBitmap
            if (bitmap != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Imagem escolhida",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = "Prompts rápidos:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickPrompts.take(2).forEach { quickPrompt ->
                    SuggestionChip(
                        onClick = { prompt = quickPrompt.prompt },
                        label = { Text(quickPrompt.title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickPrompts.drop(2).forEach { quickPrompt ->
                    SuggestionChip(
                        onClick = { prompt = quickPrompt.prompt },
                        label = { Text(quickPrompt.title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Prompt") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            ElevatedButton(
                onClick = {
                    val image = selectedBitmap

                    if (image == null) {
                        errorMessage = "Escolhe uma imagem primeiro."
                        return@ElevatedButton
                    }

                    if (prompt.isBlank()) {
                        errorMessage = "Escreve um prompt primeiro."
                        return@ElevatedButton
                    }

                    if (BuildConfig.apiKey.isBlank()) {
                        errorMessage = "API key em falta. Coloca apiKey=... no ficheiro local.properties."
                        return@ElevatedButton
                    }

                    isLoading = true
                    answer = ""
                    errorMessage = ""

                    coroutineScope.launch {
                        try {
                            answer = callGeminiWithImage(
                                bitmap = image,
                                prompt = prompt
                            )
                        } catch (e: Exception) {
                            errorMessage = "Erro: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Enviar para Gemini")
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(8.dp))
                Text("A analisar a imagem...")
            }

            if (errorMessage.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            if (answer.isNotBlank()) {
                Text(
                    text = "Resposta do Gemini:",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Text(
                        text = answer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

private suspend fun callGeminiWithImage(
    bitmap: Bitmap,
    prompt: String
): String = withContext(Dispatchers.IO) {
    val base64Image = bitmapToBase64(bitmap)

    val jsonBody = """
        {
          "contents": [
            {
              "parts": [
                {
                  "text": ${prompt.toJsonString()}
                },
                {
                  "inline_data": {
                    "mime_type": "image/jpeg",
                    "data": "$base64Image"
                  }
                }
              ]
            }
          ],
          "generationConfig": {
            "temperature": 0.2,
            "maxOutputTokens": 600
          }
        }
    """.trimIndent()

    val requestBody = jsonBody
        .toRequestBody("application/json; charset=utf-8".toMediaType())

    val request = Request.Builder()
        .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${BuildConfig.apiKey}")
        .post(requestBody)
        .build()

    val response = OkHttpClient()
        .newCall(request)
        .execute()

    val responseText = response.body?.string() ?: ""

    if (!response.isSuccessful) {
        throw Exception("HTTP ${response.code}: $responseText")
    }

    extractGeminiText(responseText)
}

private fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    val bytes = outputStream.toByteArray()
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}

private fun extractGeminiText(response: String): String {
    val root = JsonParser.parseString(response).asJsonObject

    val candidates = root.getAsJsonArray("candidates")
        ?: return "Sem candidatos na resposta."

    if (candidates.size() == 0) {
        return "Resposta vazia do Gemini."
    }

    val content = candidates[0].asJsonObject
        .getAsJsonObject("content")
        ?: return "Resposta sem conteúdo."

    val parts = content.getAsJsonArray("parts")
        ?: return "Resposta sem partes."

    if (parts.size() == 0) {
        return "Resposta sem texto."
    }

    val firstPart = parts[0].asJsonObject

    return if (firstPart.has("text")) {
        firstPart.get("text").asString
    } else {
        response
    }
}

private fun String.toJsonString(): String {
    return com.google.gson.Gson().toJson(this)
}
