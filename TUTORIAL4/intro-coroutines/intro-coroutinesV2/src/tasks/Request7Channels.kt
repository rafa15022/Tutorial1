package tasks

import contributors.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun loadContributorsChannels(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    coroutineScope {
        val repos = service
            .getOrgRepos(req.org)
            .also { logRepos(req, it) }
            .bodyList()

        if (repos.isEmpty()) {
            updateResults(emptyList(), true)
            return@coroutineScope
        }

        val channel = Channel<List<User>>(Channel.BUFFERED)

        for (repo in repos) {
            launch {
                val users = service
                    .getRepoContributors(req.org, repo.name)
                    .also { logUsers(repo, it) }
                    .bodyList()

                channel.send(users)
            }
        }

        val allUsers = mutableListOf<User>()

        repeat(repos.size) { index ->
            val users = channel.receive()
            allUsers += users
            updateResults(allUsers.aggregate(), index == repos.lastIndex)
        }

        channel.close()
    }
}
