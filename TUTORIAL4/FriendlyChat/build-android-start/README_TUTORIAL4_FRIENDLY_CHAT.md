# Friendly Chat — Tutorial 4 CM

Este projeto corresponde ao ZIP `build-android-start.zip` do Tutorial 4.

## O que já está preparado

- Firebase Authentication com Email/Password.
- Firebase Realtime Database para guardar mensagens.
- Firebase Storage para guardar imagens.
- Login Google foi desativado no código, porque exige configuração extra.
- O tema do login foi colocado como `AppThemeNoActionBar` para evitar o bug visual referido no enunciado.

## O que ainda tens obrigatoriamente de fazer

O ficheiro real `google-services.json` não pode ser gerado automaticamente por outra pessoa, porque pertence ao teu projeto Firebase.

Tens de criar o teu projeto na Firebase Console e colocar o ficheiro aqui:

```text
app/google-services.json
```

## Package name a usar no Firebase

```text
com.google.firebase.codelab.friendlychat
```

## Serviços Firebase a ativar

1. Authentication
   - Sign-in method
   - Email/Password
   - Enable

2. Realtime Database
   - Create Database
   - Para teste, podes usar regras com utilizador autenticado.

3. Storage
   - Get started
   - Para teste, podes usar regras com utilizador autenticado.

## Regras sugeridas para Realtime Database

```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}
```

## Regras sugeridas para Storage

```text
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## Como correr

1. Abrir a pasta principal no Android Studio:

```text
build-android-start
```

2. Esperar o Gradle Sync.
3. Colocar o `google-services.json` real em `app/`.
4. Correr a app num emulador ou telemóvel.
5. Criar conta por Email/Password.
6. Enviar mensagens e imagens.

## Ficheiros principais

```text
app/src/main/java/com/google/firebase/codelab/friendlychat/SignInActivity.kt
app/src/main/java/com/google/firebase/codelab/friendlychat/MainActivity.kt
app/src/main/java/com/google/firebase/codelab/friendlychat/FriendlyMessageAdapter.kt
app/src/main/java/com/google/firebase/codelab/friendlychat/model/FriendlyMessage.kt
```

## Como explicar ao professor

A aplicação usa Firebase Authentication para autenticar o utilizador com email/password. Depois de autenticado, o utilizador entra na MainActivity. As mensagens são guardadas no Realtime Database dentro do nó `messages`. Quando o utilizador envia uma imagem, primeiro é criada uma mensagem temporária com um loading image URL, depois a imagem é enviada para Firebase Storage e, quando o upload termina, o URL real da imagem substitui o temporário na Realtime Database.
