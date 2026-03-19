## 4. Implementação

### Passos de Implementação:

1. **Criação do Projeto Android**: O projeto foi configurado no Android Studio, com Kotlin como linguagem principal. A criação do projeto seguiu a configuração inicial do Android Studio, com uma **Empty Views Activity**, que fornece uma tela vazia como ponto de partida.

2. **Ajuste do Layout e Integração com o Código**: No arquivo `activity_main.xml`, foi configurado um **TextView** para exibir as informações do dispositivo. Esse **TextView** é preenchido com os dados do sistema usando o objeto `android.os.Build`.

3. **Acesso às Informações do Sistema**: As informações do dispositivo são acessadas através do objeto **android.os.Build**, que permite obter dados como:
    - Modelo do dispositivo
    - Fabricante
    - Versão do Android
    - Número de série

    O código Kotlin para extrair e mostrar essas informações é o seguinte:

    ```kotlin
    val systemInfoText = findViewById<TextView>(R.id.systemInfoText)
    val deviceInfo = "Model: ${Build.MODEL}\n" +
                     "Manufacturer: ${Build.MANUFACTURER}\n" +
                     "Android Version: ${Build.VERSION.RELEASE}\n" +
                     "Serial Number: ${Build.SERIAL}"
    systemInfoText.text = deviceInfo
    ```

4. **Execução da Aplicação**: A aplicação foi executada no **emulador Pixel 9 Pro** do Android Studio, validando a correta exibição das informações. As informações extraídas com sucesso incluem o **modelo do dispositivo**, **fabricante**, **versão do Android** e **número de série**.

### Estrutura do Projeto:

- **app**: Contém o código-fonte e recursos da aplicação.
- **build.gradle.kts**: Arquivo de configuração do Gradle para compilar e configurar o projeto.
- **activity_main.xml**: Layout da activity principal, onde a interface gráfica é configurada.
