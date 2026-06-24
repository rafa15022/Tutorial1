package dam

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * Gemini assistant using manual JSONObject construction.
 */
class AIAssistantGemini(override val properties: Properties) : AIAssistant {

    override fun getSystem() = "GEMINI"
    override val apiKeyName = "GEMINI_API_KEY"

    override var model = "gemini-2.5-flash"

    override fun buildRequest(prompt: String): Request {
        val messagesArray = JSONArray()
            .put(
                JSONObject()
                    .put("role", "user")
                    .put(
                        "parts",
                        JSONArray().put(JSONObject().put("text", prompt))
                    )
            )

        val requestJson = JSONObject()
            .put("contents", messagesArray)

        val generationConfig = JSONObject()
        val temperature = properties.getOptionalDouble("temperature")
        val maxTokens = properties.getOptionalInt("max_tokens")

        if (temperature != null) {
            generationConfig.put("temperature", temperature)
        }

        if (maxTokens != null) {
            generationConfig.put("maxOutputTokens", maxTokens)
        }

        if (generationConfig.length() > 0) {
            requestJson.put("generationConfig", generationConfig)
        }

        val requestBody = requestJson.toString()

        return Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1/models/$model:generateContent?key=$apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()
    }
}
