# Tutorial 4 - Kotlin Flows / StateFlow / Channels

Este projeto é a versão `intro-coroutinesV2` preparada para o Tutorial 4.

## Ficheiros principais alterados

- `src/contributors/Contributors.kt`
- `src/contributors/ContributorsUI.kt`
- `src/contributors/GitHubService.kt`
- `src/tasks/Aggregation.kt`
- `src/tasks/Request2Background.kt`
- `src/tasks/Request4Suspend.kt`
- `src/tasks/Request5Concurrent.kt`
- `src/tasks/Request5NotCancellable.kt`
- `src/tasks/Request6Progress.kt`
- `src/tasks/Request7Channels.kt`
- `test/contributors/MockGithubService.kt`

## O que foi implementado

### StateFlow

Foi criada a enumeração `LoadingStatus` com os estados:

- `INIT`
- `COMPLETED`
- `CANCELED`
- `IN_PROGRESS`

Foi criada a data class:

```kotlin
data class LoadingStateData(
    val status: LoadingStatus = INIT,
    val startTime: Long? = null,
    val elapsedTime: String = ""
)
```

Em `Contributors.kt` foi adicionada a propriedade pública:

```kotlin
val loadingState: StateFlow<LoadingStateData>
```

Em `ContributorsUI.kt` foi aplicado o backing property pattern:

```kotlin
private val _loadingState = MutableStateFlow(Contributors.LoadingStateData())

override val loadingState: StateFlow<Contributors.LoadingStateData> =
    _loadingState.asStateFlow()
```

Assim, a interface observa o estado e atualiza automaticamente o texto e o ícone de loading.

### Channels

Na variante `CHANNELS`, foi adicionado um segundo canal:

```kotlin
Channel<Pair<List<User>, Boolean>>(Channel.BUFFERED)
```

Este canal separa o produtor de progresso do consumidor que atualiza a interface.

## Como correr

Abrir a pasta `intro-coroutinesV2` no IntelliJ IDEA e correr:

```text
src/contributors/main.kt
```

Também podes testar pelo terminal:

```bash
./gradlew test
```

No Windows:

```bash
gradlew.bat test
```
