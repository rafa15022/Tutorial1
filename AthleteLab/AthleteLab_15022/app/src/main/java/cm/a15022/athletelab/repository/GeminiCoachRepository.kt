package cm.a15022.athletelab.repository

import cm.a15022.athletelab.model.Sport
import cm.a15022.athletelab.model.Workout
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class GeminiCoachRepository {

    private val model = Firebase.ai(
        backend = GenerativeBackend.googleAI()
    ).generativeModel(
        modelName = "gemini-2.5-flash"
    )

    suspend fun generateCoachRecommendation(workouts: List<Workout>): String {
        return withContext(Dispatchers.IO) {
            val result = withTimeoutOrNull(20000) {
                try {
                    val response = model.generateContent(buildPrompt(workouts))
                    response.text
                } catch (e: Exception) {
                    null
                }
            }

            if (result.isNullOrBlank()) {
                fallbackRecommendation(workouts)
            } else {
                result
            }
        }
    }

    private fun buildPrompt(workouts: List<Workout>): String {
        val totalWorkouts = workouts.size
        val totalMinutes = workouts.sumOf { it.durationMinutes.toInt() }

        val muscleCount = workouts.count { it.sport == Sport.MUSCLE }
        val footballCount = workouts.count { it.sport == Sport.FOOTBALL }
        val runningCount = workouts.count { it.sport == Sport.RUNNING }

        val lastWorkouts = workouts
            .sortedByDescending { it.dateMillis }
            .take(8)
            .joinToString("\n") { workout ->
                "- ${workout.sport}: ${workout.title}, ${workout.durationMinutes.toInt()} min, notas: ${workout.notes.ifBlank { "sem notas" }}"
            }

        return """
            És o Coach+ da app AthleteLab.

            Responde em português de Portugal.
            Dá uma resposta curta, clara e motivadora.

            Dados do atleta:
            - Total de treinos: $totalWorkouts
            - Tempo total: $totalMinutes minutos
            - Musculação: $muscleCount treinos
            - Futebol: $footballCount treinos
            - Atletismo: $runningCount treinos

            Últimos treinos:
            $lastWorkouts

            Cria uma resposta exatamente com esta estrutura:

            RECOMENDAÇÃO:
            Uma sugestão personalizada para o próximo treino.

            PREVISÃO:
            Uma previsão realista de evolução.

            DESAFIO INTELIGENTE:
            Um desafio semanal simples.

            Não fales de medicamentos, suplementos ou conselhos médicos.
        """.trimIndent()
    }

    private fun fallbackRecommendation(workouts: List<Workout>): String {
        if (workouts.isEmpty()) {
            return """
                RECOMENDAÇÃO:
                Regista pelo menos um treino para o Coach+ conseguir analisar a tua evolução.

                PREVISÃO:
                Quando tiveres mais treinos guardados, a previsão será mais personalizada.

                DESAFIO INTELIGENTE:
                Regista 3 treinos esta semana e escreve notas no fim de cada treino.
            """.trimIndent()
        }

        val totalWorkouts = workouts.size
        val totalMinutes = workouts.sumOf { it.durationMinutes.toInt() }

        val mainSport = when {
            workouts.count { it.sport == Sport.FOOTBALL } >= workouts.count { it.sport == Sport.MUSCLE } &&
                    workouts.count { it.sport == Sport.FOOTBALL } >= workouts.count { it.sport == Sport.RUNNING } -> "Futebol"

            workouts.count { it.sport == Sport.RUNNING } >= workouts.count { it.sport == Sport.MUSCLE } &&
                    workouts.count { it.sport == Sport.RUNNING } >= workouts.count { it.sport == Sport.FOOTBALL } -> "Atletismo"

            else -> "Musculação"
        }

        return """
            RECOMENDAÇÃO:
            O Gemini não respondeu a tempo, por isso foi usada a análise local. A tua modalidade mais frequente é $mainSport. Mantém a consistência e tenta melhorar um detalhe no próximo treino.

            PREVISÃO:
            Já tens $totalWorkouts treinos e $totalMinutes minutos registados. Se continuares assim, vais conseguir acompanhar melhor a tua evolução semanal.

            DESAFIO INTELIGENTE:
            Faz mais 2 treinos esta semana e adiciona notas no final para o Coach+ conseguir analisar melhor o teu progresso.
        """.trimIndent()
    }
}