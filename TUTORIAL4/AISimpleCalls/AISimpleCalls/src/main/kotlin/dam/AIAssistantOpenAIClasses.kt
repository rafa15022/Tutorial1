package dam

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

/**
 * OpenAI assistant using Kotlin data classes serialized with Gson.
 */
class AIAssistantOpenAIClasses(override val properties: Properties) : AIAssistant {

    override fun getSystem() = "OPENAI"
    override val apiKeyName = "OPENAI_API_KEY"

    override var model = "gpt-4o"

    data class Message(
        val role: String,
        val content: String
    )

    data class OpenAIRequest(
        val model: String,
        val messages: List<Message>,
        val temperature: Double? = null,
        val max_tokens: Int? = null,
        val top_p: Double = 1.0,
        val frequency_penalty: Double = 0.0,
        val presence_penalty: Double = 0.0
    )

    private val gson = Gson()

    override fun buildRequest(prompt: String): Request {
        val messages = listOf(
            Message(
                role = "system",
                content = "You are a friendly and helpful assistant."
            ),
            Message(
                role = "user",
                content = prompt
            )
        )

        val openAIRequest = OpenAIRequest(
            model = model,
            messages = messages,
            temperature = properties.getOptionalDouble("temperature"),
            max_tokens = properties.getOptionalInt("max_tokens")
        )

        val requestBody = gson.toJson(openAIRequest)

        return Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()
    }
}
