package cm.a15022.athletelab.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import cm.a15022.athletelab.R
import cm.a15022.athletelab.model.Workout
import cm.a15022.athletelab.repository.AuthRepository
import cm.a15022.athletelab.repository.GeminiCoachRepository
import cm.a15022.athletelab.repository.WorkoutRepository
import kotlinx.coroutines.launch

class CoachFragment : Fragment() {

    private val authRepository = AuthRepository()
    private val workoutRepository = WorkoutRepository()
    private val geminiCoachRepository = GeminiCoachRepository()

    private var lastWorkouts: List<Workout> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_coach, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val uid = authRepository.currentUser()?.uid ?: return

        val activateButton = view.findViewById<Button>(R.id.activatePremiumButton)
        val generateButton = view.findViewById<Button>(R.id.generateCoachButton)

        val recommendationTitle = view.findViewById<TextView>(R.id.recommendationTitleText)
        val recommendationBody = view.findViewById<TextView>(R.id.recommendationBodyText)
        val predictionTitle = view.findViewById<TextView>(R.id.predictionTitleText)
        val predictionBody = view.findViewById<TextView>(R.id.predictionBodyText)
        val challengeTitle = view.findViewById<TextView>(R.id.challengeTitleText)
        val challengeBody = view.findViewById<TextView>(R.id.challengeBodyText)

        recommendationTitle.text = "Coach+ AI"
        recommendationBody.text = "Clica em “Gerar recomendação” para receber uma análise personalizada."

        predictionTitle.text = "Previsão"
        predictionBody.text = "A previsão será criada com base nos teus treinos guardados."

        challengeTitle.text = "Desafio inteligente"
        challengeBody.text = "O desafio será criado automaticamente pelo Coach+."

        activateButton.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Coach+ Premium académico ativo",
                Toast.LENGTH_SHORT
            ).show()
        }

        workoutRepository.observeMyWorkouts(uid) { workouts ->
            lastWorkouts = workouts
        }

        generateButton.setOnClickListener {
            lifecycleScope.launch {
                generateButton.isEnabled = false
                generateButton.text = "A gerar com Gemini..."

                recommendationTitle.text = "Coach+ AI"
                recommendationBody.text = "A analisar os teus treinos..."
                predictionBody.text = "A aguardar resposta..."
                challengeBody.text = "A criar desafio..."

                val result = geminiCoachRepository.generateCoachRecommendation(lastWorkouts)

                showCoachResult(
                    result,
                    recommendationTitle,
                    recommendationBody,
                    predictionTitle,
                    predictionBody,
                    challengeTitle,
                    challengeBody
                )

                generateButton.text = "Gerar recomendação"
                generateButton.isEnabled = true

                Toast.makeText(
                    requireContext(),
                    "Recomendação gerada",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showCoachResult(
        result: String,
        recommendationTitle: TextView,
        recommendationBody: TextView,
        predictionTitle: TextView,
        predictionBody: TextView,
        challengeTitle: TextView,
        challengeBody: TextView
    ) {
        val recommendation = extractSection(
            result,
            "RECOMENDAÇÃO:",
            "PREVISÃO:"
        )

        val prediction = extractSection(
            result,
            "PREVISÃO:",
            "DESAFIO INTELIGENTE:"
        )

        val challenge = extractSection(
            result,
            "DESAFIO INTELIGENTE:",
            null
        )

        recommendationTitle.text = "Recomendação AI"
        recommendationBody.text = recommendation.ifBlank { result }

        predictionTitle.text = "Previsão AI"
        predictionBody.text = prediction.ifBlank { "Previsão não encontrada na resposta." }

        challengeTitle.text = "Desafio inteligente AI"
        challengeBody.text = challenge.ifBlank { "Desafio não encontrado na resposta." }
    }

    private fun extractSection(
        text: String,
        start: String,
        end: String?
    ): String {
        val startIndex = text.indexOf(start)

        if (startIndex == -1) {
            return ""
        }

        val contentStart = startIndex + start.length

        val endIndex = if (end != null) {
            text.indexOf(end, contentStart)
        } else {
            -1
        }

        return if (endIndex != -1) {
            text.substring(contentStart, endIndex).trim()
        } else {
            text.substring(contentStart).trim()
        }
    }
}