package dam

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * OpenAI assistant using manual JSONObject construction.
 */
class AIAssistantOpenAI(override val properties: Properties) : AIAssistant {

    override fun getSystem() = "OPENAI"
    override val apiKeyName = "OPENAI_API_KEY"

    override var model = "gpt-4o"

    override fun buildRequest(prompt: String): Request {
        val messagesArray = JSONArray()
            .put(
                JSONObject()
                    .put("role", "system")
                    .put("content", "You are a friendly and helpful assistant.")
            )
            .put(
                JSONObject()
                    .put("role", "user")
                    .put("content", prompt)
            )

        val requestJson = JSONObject()
            .put("model", model)
            .put("messages", messagesArray)

        val temperature = properties.getOptionalDouble("temperature")
        val maxTokens = properties.getOptionalInt("max_tokens")

        if (temperature != null) {
            requestJson.put("temperature", temperature)
        }

        if (maxTokens != null) {
            requestJson.put("max_tokens", maxTokens)
        }

        val requestBody = requestJson.toString()

        return Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()
    }
}
