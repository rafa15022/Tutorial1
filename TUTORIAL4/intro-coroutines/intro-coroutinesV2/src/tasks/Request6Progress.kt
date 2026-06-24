package tasks

import contributors.*

suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .bodyList()

    if (repos.isEmpty()) {
        updateResults(emptyList(), true)
        return
    }

    val allUsers = mutableListOf<User>()

    for ((index, repo) in repos.withIndex()) {
        val users = service
            .getRepoContributors(req.org, repo.name)
            .also { logUsers(repo, it) }
            .bodyList()

        allUsers += users
        updateResults(allUsers.aggregate(), index == repos.lastIndex)
    }
}
