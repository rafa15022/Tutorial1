# NotesProXMLViews3 - Tutorial 4 pronto

Este projeto foi preparado para a parte **Firebase / Notes Pro** do Tutorial 4.

## O que já está feito no código

- Firebase Authentication para criar conta e fazer login com email/password.
- Verificação de email antes de entrar na app.
- Cloud Firestore para guardar as notas do utilizador autenticado.
- Lista de notas com RecyclerView.
- Criar nota.
- Editar nota.
- Apagar nota.
- Logout no menu.
- Extra: imagem opcional em cada nota **sem usar Firebase Storage**.
  - Como o Storage pode pedir plano Blaze, a imagem é guardada em Base64 comprimido no documento Firestore.
  - Isto é simples para demonstração, mas só deve ser usado com imagens pequenas.

## O que ainda tens de fazer no teu Firebase

1. Ir à Firebase Console.
2. Criar um projeto, por exemplo: `NotesProCM`.
3. Adicionar uma app Android.
4. Usar este package name:

```text
com.notes.notesproxmlviews
```

5. Descarregar o ficheiro `google-services.json`.
6. Colocar o ficheiro real aqui:

```text
NotesProXMLViews3/app/google-services.json
```

7. Ativar Authentication:

```text
Authentication > Sign-in method > Email/Password > Enable
```

8. Criar Cloud Firestore:

```text
Firestore Database > Create database > Start in test mode
```

9. Abrir o projeto no Android Studio e fazer Sync Gradle.
10. Correr a app.

## Regras Firestore simples para teste

Estas regras servem para testar durante o trabalho. Não são regras finais de produção.

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /notes/{userId}/my_notes/{noteId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## Como testar

1. Abrir a app.
2. Criar conta com email real.
3. Confirmar o email.
4. Fazer login.
5. Criar uma nota.
6. Opcionalmente escolher uma imagem pequena.
7. Guardar.
8. Verificar no Firebase Console se a nota aparece em:

```text
Firestore Database > notes > UID_DO_USER > my_notes
```

## O que dizer ao professor

A app usa Firebase Authentication para login e registo com email/password. Depois de autenticado, cada utilizador tem a sua própria subcoleção de notas no Cloud Firestore. A lista de notas é atualizada com um listener em tempo real. O extra da imagem opcional foi implementado sem Cloud Storage, guardando uma imagem comprimida em Base64 dentro do documento Firestore, porque o Storage pode exigir upgrade para o plano Blaze em projetos Firebase novos.
