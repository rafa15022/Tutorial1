# AthleteLab - Android Kotlin + Firebase

## Mapa de navegação

AuthActivity (login/registo)
-> MainActivity
   -> HomeFragment
   -> WorkoutsFragment -> AddWorkoutActivity -> retorna título do treino criado
   -> ChallengesFragment
   -> FeedFragment -> PostEditorActivity -> retorna id da publicação
   -> ProfileFragment -> EditProfileActivity -> retorna nome atualizado
   -> Menu superior: CalendarFragment, ProgressFragment, CoachFragment, HelpActivity, AboutActivity, SettingsActivity, Logout

## Ecrãs principais

1. Login/Registo
2. Home/Dashboard
3. Treinos
4. Calendário
5. Evolução/Gráficos
6. Desafios
7. Feed social
8. Perfil
9. Coach+ Premium
10. Ajuda
11. Sobre
12. Definições/Conta

## Estrutura Firebase Firestore

users/{userId}
- userId, name, email, favoriteSport, photoUrl, premium, friends, groups, createdAt

workouts/{workoutId}
- id, userId, sport, title, dateMillis, durationMinutes, notes
- campos específicos de musculação: exercise, sets, reps, loadKg
- campos específicos de futebol: goals, assists, minutesPlayed, position, intensity
- campos específicos de atletismo: distanceKm, timeMinutes, pace, trainingType

challenges/{challengeId}
- id, userId, groupId, title, sport, type, scope, target, progress, status, dueDateMillis, createdAt

feedPosts/{postId}
- id, userId, authorName, text, sport, result, visibility, imageUrl, createdAt

groups/{groupId}
- preparado para grupos/amigos

coachRecommendations/{recommendationId}
- id, userId, sport, recommendation, goalPrediction, smartChallenge, createdAt

## Pastas principais

app/src/main/java/cm/a15022/athletelab/model
- Modelos de dados: UserProfile, Workout, Challenge, FeedPost, ProgressData, CoachRecommendation.

app/src/main/java/cm/a15022/athletelab/repository
- Lógica de Firebase separada da UI: AuthRepository, UserRepository, WorkoutRepository, ChallengeRepository, FeedRepository, CoachRepository.

app/src/main/java/cm/a15022/athletelab/ui
- Activities, Fragments e gráfico customizado.

app/src/main/java/cm/a15022/athletelab/ui/adapters
- Adapters dos RecyclerViews.

app/src/main/java/cm/a15022/athletelab/utils
- DateUtils, LocalStateManager, UiUtils.

app/src/main/res/values, values-en, values-es
- Suporte multilingue: português, inglês e espanhol.

## Funcionalidades gratuitas

- Login/registo.
- Registo de treinos.
- Calendário.
- Feed social.
- Desafios base.
- Gráficos simples.
- Perfil.

## Funcionalidades premium Coach+

- Recomendações personalizadas.
- Previsão de metas.
- Desafios inteligentes.
- Preço académico assumido: 1€/mês.

## Configuração Firebase obrigatória

1. Criar projeto no Firebase Console.
2. Ativar Authentication -> Email/Password.
3. Criar Cloud Firestore.
4. Ativar Firebase Storage se quiseres guardar avatar e imagens de publicações.
5. Adicionar app Android com package: cm.a15022.athletelab.
6. Transferir google-services.json real.
7. Substituir app/google-services.json deste projeto pelo ficheiro real.
8. Publicar firestore.rules e storage.rules na consola Firebase.

## Onde está cada requisito

- Login e registo: auth/AuthActivity.kt + repository/AuthRepository.kt.
- Estado remoto Firebase: repositories usam Firestore/Auth/Storage.
- Estado local: utils/LocalStateManager.kt.
- Estado privado: workouts do próprio userId, perfil, coachRecommendations.
- Estado partilhado: feedPosts públicos/amigos e challenges com scope group.
- 4+ ecrãs: Home, Treinos, Calendário, Evolução, Desafios, Feed, Perfil, Coach+.
- 3 atividades/fragments com passagem/retorno: AddWorkoutActivity, PostEditorActivity, EditProfileActivity.
- Imagens pequenas/grandes: ícones vetoriais, avatar, preview de post e hero cards.
- Animação: res/anim/pulse.xml aplicada no logo em AuthActivity.
- Menu inicial/top menu: MainActivity + main_menu.xml.
- Ajuda/About/Settings: HelpActivity, AboutActivity, SettingsActivity.
- Multilingue: values, values-en, values-es.
- Coach+ Premium: CoachFragment.kt + CoachRepository.kt.

## Nota de atualização 15022

O package name foi atualizado para:

```text
cm.a15022.athletelab
```

Ao criar a app Android no Firebase, usa exatamente este package name. O ficheiro `google-services.json` real deve ser colocado em:

```text
app/google-services.json
```

Nesta versão foram removidas as chamadas reais ao Firebase Storage para evitar a necessidade de ativar o plano Blaze. A aplicação usa Firebase Authentication e Firestore.
