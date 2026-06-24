package dam

import kotlinx.coroutines.runBlocking

/**
 * Main entry point for the LLM Assistant application.
 * It supports normal questions and sentiment analysis.
 */
fun main() = runBlocking {
    println("\n🤖 Starting LLM Assistant application...\n")

    val properties = getProperties()

    configureLogging(properties)
    println()

    println("✨ Using AI_LLM: ${properties.getProperty("AI_LLM")}")

    val assistant: AIAssistant = AIAssistantFactory.createAssistant(properties)
    println()

    println("✨ Using: ${assistant.getSystem()} ${assistant.model}\n")

    println("💬 Modes:")
    println("   1 - Normal question")
    println("   2 - Sentiment analysis, output in JSON")
    println("   0 - Exit")
    println()

    while (true) {
        println("➖➖➖➖➖➖➖➖➖➖")
        print("Choose mode [1/2/0]: ")

        val mode = readlnOrNull() ?: break

        if (mode == "0") {
            break
        }

        if (mode != "1" && mode != "2") {
            println("⚠️ Invalid mode. Choose 1, 2 or 0.")
            continue
        }

        print("🧠 Write your text: ")
        val input = readlnOrNull() ?: break

        if (input.isBlank()) {
            println("⚠️ Please enter text or press Ctrl+D to exit.")
            continue
        }

        try {
            val output = when (mode) {
                "1" -> assistant.processInput(input)
                "2" -> assistant.processSentimentAnalysis(input)
                else -> "Invalid mode"
            }

            println("\n🤖 Answer:\n$output\n")
        } catch (e: Exception) {
            println("\n❌ API call failed.")
            println("This is usually caused by an invalid API key, missing quota, rate limit, unavailable model or internet/API access problem.")
            println("Details:")
            println(e.message)
            println()
        }
    }

    println("\n👋 Thank you for using LLM Assistant. Goodbye!")
}
