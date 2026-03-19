# Assignment 1 – Construção de uma App de Informações do Sistema
Curso: Computação Móvel  
Aluno: Rafael Falcão
Data: 19/03/2026
URL do Repositório: 

---

## 1. Introdução
Este trabalho tem como objetivo desenvolver uma aplicação Android chamada **SystemInfoApp**, que exibe informações sobre o dispositivo atual utilizando o objeto **android.os.Build**. A aplicação faz uso de um widget de múltiplas linhas para mostrar as propriedades do sistema, como modelo, fabricante, versão, entre outros.

## 2. Visão Geral do Sistema
A aplicação **SystemInfoApp** foi criada para exibir informações detalhadas sobre o dispositivo em que está sendo executada. Essas informações são extraídas do sistema operacional Android usando o objeto **android.os.Build**. A interface da aplicação é simples e foi desenvolvida utilizando widgets como o **TextView**, que permite exibir as informações extraídas de forma organizada.

A aplicação tem como objetivo fornecer ao utilizador detalhes sobre o sistema do dispositivo, como:
- Modelo do dispositivo
- Fabricante
- Versão do Android
- Número de série
- Outras informações relevantes do sistema

## 3. Arquitetura e Design
A arquitetura da aplicação é baseada no uso de uma única **Activity**, onde as informações do sistema são carregadas e exibidas ao utilizador. A interface é composta principalmente por um **TextView** para mostrar as informações.

### Arquitetura:
- **Activity**: A aplicação é composta por uma única activity que contém a lógica para exibir as informações.
- **Widget TextView**: Utilizado para mostrar as informações do sistema de forma clara e estruturada.

A aplicação segue o padrão básico de estruturação de um projeto Android com Kotlin, aproveitando os componentes padrão do Android Studio, como o **Gradle** e os arquivos de configuração para garantir a compatibilidade e otimização da aplicação.

## 4. Implementação

### Passos de Implementação:
1. **Criação do Projeto Android**: O projeto foi configurado no Android Studio, com Kotlin como linguagem principal. A criação do projeto seguiu a configuração inicial do Android Studio, com um **Empty Views Activity**.
   
2. **Acesso às Informações do Sistema**: A informação sobre o dispositivo foi extraída usando o objeto `android.os.Build`. Exemplo de código:

    ```kotlin
    val deviceInfo = "Model: ${Build.MODEL}\n" +
                     "Manufacturer: ${Build.MANUFACTURER}\n" +
                     "Android Version: ${Build.VERSION.RELEASE}\n" +
                     "Serial Number: ${Build.SERIAL}"
    ```

3. **Exibição das Informações**: Utilizamos um **TextView** para exibir as informações extraídas do sistema. O layout foi configurado no arquivo `activity_main.xml`.

    ```xml
    <TextView
        android:id="@+id/systemInfoText"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="18sp"
        android:padding="16dp"/>
    ```

4. **Execução da Aplicação**: Após implementar o código para acessar as informações e exibi-las no **TextView**, a aplicação foi executada no emulador do Android Studio (Pixel 9 Pro) para verificar o funcionamento.

### Código Exemplo (MainActivity.kt):

```kotlin
package com.example.systeminfoapp

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val systemInfoText = findViewById<TextView>(R.id.systemInfoText)
        val deviceInfo = "Model: ${Build.MODEL}\n" +
                         "Manufacturer: ${Build.MANUFACTURER}\n" +
                         "Android Version: ${Build.VERSION.RELEASE}\n" +
                         "Serial Number: ${Build.SERIAL}"
        systemInfoText.text = deviceInfo
    }
}
## 5. Testes e Validação

### Estratégia de Testes:

*   **Testes de Funcionalidade**: A principal funcionalidade testada foi a exibição das informações corretas do dispositivo. Foram realizados testes em dispositivos e emuladores para verificar a precisão das informações exibidas.
    
*   **Testes no Emulador**: O emulador Pixel 9 Pro foi utilizado para simular o dispositivo Android e validar a exibição das informações. Todos os dados (modelo, fabricante, versão Android, etc.) foram mostrados corretamente.
    
*   **Testes em Dispositivo Real**: A aplicação também foi testada em um dispositivo físico, garantindo que o comportamento fosse o mesmo.

### Resultados Esperados:

O aplicativo exibe as seguintes informações no **TextView**:

*   Modelo do dispositivo
    
*   Fabricante
    
*   Versão do Android
    
*   Número de série

## 6. Instruções de Uso

Para executar o projeto, siga os seguintes passos:

1.  **Instalar o Android Studio**: Baixe e instale o Android Studio no seu computador.
    
2.  **Abrir o Projeto**: Clone o repositório ou abra o projeto diretamente no Android Studio.
    
3.  **Executar a Aplicação**: Conecte um dispositivo Android ou use um emulador para rodar o aplicativo.
    
4.  **Exibir as Informações**: Após executar o aplicativo, as informações do sistema serão exibidas na tela.
