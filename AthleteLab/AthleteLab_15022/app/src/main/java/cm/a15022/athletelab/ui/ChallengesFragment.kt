package cm.a15022.athletelab.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cm.a15022.athletelab.R
import cm.a15022.athletelab.repository.AuthRepository
import cm.a15022.athletelab.repository.ChallengeRepository
import cm.a15022.athletelab.ui.adapters.ChallengeAdapter
import kotlinx.coroutines.launch

class ChallengesFragment : Fragment() {

    private val authRepository = AuthRepository()
    private val challengeRepository = ChallengeRepository()

    private lateinit var adapter: ChallengeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_challenges, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val uid = authRepository.currentUser()?.uid ?: return

        adapter = ChallengeAdapter { challenge ->
            lifecycleScope.launch {
                try {
                    challengeRepository.updateProgress(challenge, 1.0)

                    Toast.makeText(
                        requireContext(),
                        getString(R.string.progress_updated),
                        Toast.LENGTH_SHORT
                    ).show()

                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Erro ao atualizar progresso",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val recycler = view.findViewById<RecyclerView>(R.id.challengesRecycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        val generateButton = view.findViewById<Button>(R.id.createChallengeButton)

        generateButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    generateButton.isEnabled = false
                    generateButton.text = "A gerar desafios..."

                    challengeRepository.resetDefaultChallenges(uid)

                    Toast.makeText(
                        requireContext(),
                        "Desafios base recriados",
                        Toast.LENGTH_SHORT
                    ).show()

                    generateButton.text = getString(R.string.generate_base_challenges)
                    generateButton.isEnabled = true

                } catch (e: Exception) {
                    generateButton.text = getString(R.string.generate_base_challenges)
                    generateButton.isEnabled = true

                    Toast.makeText(
                        requireContext(),
                        "Erro ao gerar desafios",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        challengeRepository.observeChallenges(uid) { challenges ->
            adapter.submitList(challenges)
        }
    }
}