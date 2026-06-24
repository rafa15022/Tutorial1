package dam

import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Properties
import kotlin.math.pow

/**
 * Common interface for every LLM assistant implementation.
 * The concrete classes only build the provider-specific HTTP request.
 * This interface handles prompt creation, retry logic and response extraction.
 */
interface AIAssistant {

    val properties: Properties

    val logger: Logger
        get() = LoggerFactory.getLogger(this::class.java)

    val apiKeyName: String

    var model: String

    val client: OkHttpClient
        get() = OkHttpClient()

    val apiKey: String
        get() = properties.getProperty(apiKeyName)
            ?: throw IllegalStateException("API key $apiKeyName not found in configuration file.")

    fun getSystem(): String

    /**
     * Normal question mode.
     */
    suspend fun processInput(input: String): String {
        val formattedPrompt = buildPrompt(input)
        return apiCallWithBackoff(formattedPrompt)
    }

    /**
     * Sentiment analysis mode required by Tutorial 4.
     * The answer must be only valid JSON with rating and justification.
     */
    suspend fun processSentimentAnalysis(input: String): String {
        val prompt = buildSentimentPrompt(input)
        return apiCallWithBackoff(prompt)
    }

    fun buildPrompt(input: String): String {
        return """
            Your name is Assistant.
            The preferred language is Portuguese from Portugal.
            Respond in a friendly, simple and helpful manner.
            The user's request is: "$input"
        """.trimIndent()
    }

    fun buildSentimentPrompt(input: String): String {
        return """
            Analisa o sentimento do texto seguinte numa escala de 1 a 7.

            Escala:
            1 - Muito negativo
            2 - Negativo
            3 - Ligeiramente negativo
            4 - Neutro
            5 - Ligeiramente positivo
            6 - Positivo
            7 - Muito positivo

            Responde apenas em JSON válido, sem markdown, sem texto extra e exatamente neste formato:
            {
              "rating": valor,
              "justification": "valor"
            }

            Texto:
            "$input"
        """.trimIndent()
    }

    suspend fun apiCallWithBackoff(input: String): String {
        var attempts = 0
        val maxAttempts = 5
        val baseDelay = 1000L
        var lastError: Exception? = null

        while (attempts < maxAttempts) {
            try {
                return makeApiCall(input)
            } catch (e: Exception) {
                lastError = e
                println("⚠️ API attempt ${attempts + 1}/$maxAttempts failed:")
                println(e.message)

                if (e.message?.contains("429") == true) {
                    attempts++

                    val delayTime = baseDelay * (2.0.pow(attempts.toDouble())).toLong()
                    println("⏳ Rate limit/quota error. Retrying in ${delayTime} ms...")
                    delay(delayTime)
                } else {
                    throw e
                }
            }
        }

        throw Exception(
            "Exceeded maximum retry attempts. Last error was:\n${lastError?.message}",
            lastError
        )
    }

    fun makeApiCall(prompt: String): String {
        logger.info("Prompt:\n$prompt")

        val request = buildRequest(prompt)

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string()
                throw Exception("Error in API call: ${response.code} - ${response.message}\nResponse: $errorBody")
            }

            val responseBody = response.body?.string() ?: return "Error: empty response"

            return try {
                val json = JSONObject(responseBody)
                logger.debug("Raw API response: {}", responseBody)

                when (getSystem()) {
                    "OPENAI" -> extractOpenAIText(json)
                    "GEMINI" -> extractGeminiText(json)
                    else -> responseBody
                }
            } catch (e: JSONException) {
                val truncatedResponse = if (responseBody.length > 200) {
                    "${responseBody.substring(0, 200)}..."
                } else {
                    responseBody
                }

                logger.error("Error parsing JSON response: ${e.message}")
                logger.error("Response body (truncated): $truncatedResponse")

                throw Exception("Failed to parse API response: ${e.message}", e)
            }
        }
    }

    private fun extractGeminiText(json: JSONObject): String {
        if (!json.has("candidates") || json.getJSONArray("candidates").length() == 0) {
            return "Error: No candidates found in the Gemini API response"
        }

        val firstCandidate = json.getJSONArray("candidates").getJSONObject(0)

        if (!firstCandidate.has("content")) {
            return "Error: No content found in the Gemini API response"
        }

        val content = firstCandidate.getJSONObject("content")

        if (!content.has("parts") || content.getJSONArray("parts").length() == 0) {
            return "Error: No parts found in the Gemini API response"
        }

        val firstPart = content.getJSONArray("parts").getJSONObject(0)

        if (!firstPart.has("text")) {
            return "Error: No text found in the Gemini API response"
        }

        return firstPart.getString("text").trim()
    }

    private fun extractOpenAIText(json: JSONObject): String {
        if (!json.has("choices") || json.getJSONArray("choices").length() == 0) {
            return "Error: No choices found in the OpenAI API response"
        }

        val firstChoice = json.getJSONArray("choices").getJSONObject(0)

        if (!firstChoice.has("message")) {
            return "Error: No message found in the OpenAI API response"
        }

        val message = firstChoice.getJSONObject("message")

        if (!message.has("content")) {
            return "Error: No content found in the OpenAI API response"
        }

        return message.getString("content").trim()
    }

    fun buildRequest(prompt: String): Request
}
