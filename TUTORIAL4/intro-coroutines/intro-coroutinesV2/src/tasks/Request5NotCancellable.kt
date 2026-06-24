package tasks

import contributors.*
import kotlinx.coroutines.*

suspend fun loadContributorsNotCancellable(
    service: GitHubService,
    req: RequestData
): List<User> = withContext(NonCancellable) {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) }
        .bodyList()

    repos.flatMap { repo ->
        service
            .getRepoContributors(req.org, repo.name)
            .also { logUsers(repo, it) }
            .bodyList()
    }.aggregate()
}
