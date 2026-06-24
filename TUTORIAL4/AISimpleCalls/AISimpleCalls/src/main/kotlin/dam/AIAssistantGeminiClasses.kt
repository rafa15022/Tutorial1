package dam

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

/**
 * Gemini assistant using Kotlin data classes serialized with Gson.
 */
class AIAssistantGeminiClasses(override val properties: Properties) : AIAssistant {

    override fun getSystem() = "GEMINI"
    override val apiKeyName = "GEMINI_API_KEY"

    override var model = "gemini-2.5-flash"

    data class Part(
        val text: String
    )

    data class Content(
        val role: String,
        val parts: List<Part>
    )

    data class GeminiRequest(
        val contents: List<Content>,
        val generationConfig: GenerationConfig? = null
    )

    data class GenerationConfig(
        val temperature: Double? = null,
        val topK: Int? = 40,
        val topP: Double? = 0.95,
        val maxOutputTokens: Int? = null,
        val candidateCount: Int? = 1
    )

    private val gson = Gson()

    override fun buildRequest(prompt: String): Request {
        val content = Content(
            role = "user",
            parts = listOf(Part(text = prompt))
        )

        val temperature = properties.getOptionalDouble("temperature")
        val maxTokens = properties.getOptionalInt("max_tokens")

        val generationConfig = if (temperature != null || maxTokens != null) {
            GenerationConfig(
                temperature = temperature,
                maxOutputTokens = maxTokens
            )
        } else {
            null
        }

        val geminiRequest = GeminiRequest(
            contents = listOf(content),
            generationConfig = generationConfig
        )

        val requestBody = gson.toJson(geminiRequest)

        return Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1/models/$model:generateContent?key=$apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()
    }
}
