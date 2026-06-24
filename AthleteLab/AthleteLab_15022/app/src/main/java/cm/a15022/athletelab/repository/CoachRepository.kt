package cm.a15022.athletelab.repository

import cm.a15022.athletelab.model.CoachRecommendation
import cm.a15022.athletelab.model.Sport
import cm.a15022.athletelab.model.Workout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Locale

class CoachRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun generateRecommendation(userId: String, sport: String, workouts: List<Workout>): CoachRecommendation {
        val count = workouts.size
        val totalMinutes = workouts.sumOf { it.durationMinutes }
        val avg = if (count > 0) totalMinutes / count else 0.0
        val language = Locale.getDefault().language

        val recommendation = when (language) {
            "en" -> when (sport) {
                Sport.MUSCLE -> "Keep 3 workouts per week and increase load gradually."
                Sport.FOOTBALL -> "Include at least 2 technical sessions and 1 intensity session per week to improve match consistency."
                else -> "Alternate easy runs with one pace session. The goal is to increase volume without losing recovery."
            }

            "es" -> when (sport) {
                Sport.MUSCLE -> "Mantén 3 entrenamientos por semana y aumenta la carga de forma controlada."
                Sport.FOOTBALL -> "Incluye al menos 2 sesiones técnicas y 1 sesión de intensidad por semana para mejorar la constancia en partido."
                else -> "Alterna carrera suave con una sesión de ritmo. El objetivo es aumentar volumen sin perder recuperación."
            }

            else -> when (sport) {
                Sport.MUSCLE -> "Mantém 3 treinos por semana e tenta subir a carga de forma controlada."
                Sport.FOOTBALL -> "Inclui pelo menos 2 sessões técnicas e 1 sessão de intensidade por semana para melhorar consistência em jogo."
                else -> "Alterna corrida leve com uma sessão de ritmo. O objetivo é aumentar volume sem perder recuperação."
            }
        }

        val prediction = when (language) {
            "en" -> if (count >= 3) {
                "Based on your $count workouts and ${avg.toInt()} min average, you may improve in the next few weeks."
            } else {
                "Log at least 3 workouts so Coach+ can create stronger predictions."
            }

            "es" -> if (count >= 3) {
                "Según tus $count entrenamientos y media de ${avg.toInt()} min, podrás mejorar en las próximas semanas."
            } else {
                "Registra al menos 3 entrenamientos para que Coach+ cree predicciones más fuertes."
            }

            else -> if (count >= 3) {
                "Com base nos teus $count treinos e média de ${avg.toInt()} min, poderás melhorar nas próximas semanas."
            } else {
                "Regista pelo menos 3 treinos para o Coach+ criar previsões mais fortes."
            }
        }

        val smartChallenge = when (language) {
            "en" -> when (sport) {
                Sport.MUSCLE -> "Smart challenge: complete 4 sets in the main exercise and log load in every workout."
                Sport.FOOTBALL -> "Smart challenge: complete 3 technical sessions and log minutes, goals or assists."
                else -> "Smart challenge: run 12 km in total this week and log average pace."
            }

            "es" -> when (sport) {
                Sport.MUSCLE -> "Reto inteligente: completar 4 series en el ejercicio principal y registrar carga en todos los entrenamientos."
                Sport.FOOTBALL -> "Reto inteligente: hacer 3 entrenamientos técnicos y registrar minutos, goles o asistencias."
                else -> "Reto inteligente: correr 12 km acumulados esta semana y registrar el ritmo medio."
            }

            else -> when (sport) {
                Sport.MUSCLE -> "Desafio inteligente: completar 4 séries no exercício principal e registar carga em todos os treinos."
                Sport.FOOTBALL -> "Desafio inteligente: realizar 3 treinos técnicos e registar minutos, golos ou assistências."
                else -> "Desafio inteligente: correr 12 km acumulados esta semana e registar o ritmo médio."
            }
        }

        val coach = CoachRecommendation(
            userId = userId,
            sport = sport,
            recommendation = recommendation,
            goalPrediction = prediction,
            smartChallenge = smartChallenge
        )
        val ref = db.collection("coachRecommendations").document()
        coach.id = ref.id
        ref.set(coach).await()
        return coach
    }
}
