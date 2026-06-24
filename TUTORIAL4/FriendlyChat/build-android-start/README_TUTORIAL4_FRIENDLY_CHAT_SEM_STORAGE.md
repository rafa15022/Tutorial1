# Friendly Chat - Tutorial 4 sem Firebase Storage

Esta versão foi adaptada para funcionar sem Firebase Storage, porque em projetos Firebase novos o Storage pode exigir upgrade para o plano Blaze.

## Serviços Firebase usados

- Firebase Authentication: login/registo com Email/Password
- Firebase Realtime Database: guardar mensagens de texto em tempo real

## O que foi removido

- Botão de escolher imagem no chat
- Upload de imagens para Firebase Storage
- Dependência `com.google.firebase:firebase-storage`
- Código que fazia `putFile(uri)` para o Storage

## O que continua a funcionar

- Login com Email/Password
- Envio de mensagens de texto
- Listagem das mensagens em tempo real
- Logout

## Passos para configurar

1. Criar projeto na Firebase Console.
2. Adicionar app Android com package:

```text
com.google.firebase.codelab.friendlychat
```

3. Fazer download do `google-services.json`.
4. Colocar o ficheiro em:

```text
app/google-services.json
```

5. Ativar:

```text
Authentication > Email/Password
Realtime Database > Create Database > Test mode
```

6. Abrir o projeto no Android Studio.
7. Fazer Sync Gradle.
8. Correr a app.

## Regras simples para teste na Realtime Database

```json
{
  "rules": {
    "messages": {
      ".read": "auth != null",
      ".write": "auth != null"
    }
  }
}
```

## Explicação para defesa

A app Friendly Chat foi adaptada para usar Firebase Authentication e Realtime Database, mantendo a funcionalidade principal de chat em tempo real. A parte de Firebase Storage foi removida porque, no plano gratuito Spark, a criação do bucket de Storage pode exigir upgrade para Blaze. Assim, a app continua funcional para login e mensagens de texto, que são guardadas na Realtime Database.
