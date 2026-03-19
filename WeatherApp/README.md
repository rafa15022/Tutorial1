# Assignment 1 – WeatherApp: Aplicação Meteorológica com Mapa
Curso: Computação Móvel  
Aluno: Rafael Falcão
Data: 19/03/2026
URL do Repositório:

---

## 1. Introdução
Este trabalho tem como objetivo a criação de uma aplicação Android chamada **WeatherApp**, que permite ao utilizador consultar informações meteorológicas de um local específico. A aplicação utiliza um mapa para localizar o local e, ao tocar sobre ele, exibe as informações do clima, como temperatura, condições atmosféricas e outras informações relevantes.

## 2. Visão Geral do Sistema
A **WeatherApp** utiliza a API do Google Maps para exibir um mapa interativo onde o utilizador pode selecionar um local e visualizar informações meteorológicas de acordo com a localização escolhida. A aplicação utiliza também a API de Clima para recuperar os dados meteorológicos.

Funcionalidades principais da aplicação incluem:
- Mapa interativo com localização geográfica.
- Exibição das condições meteorológicas (temperatura, condições do tempo, etc.).
- Visualização de informações detalhadas sobre o clima de um local selecionado.

## 3. Arquitetura e Design
A aplicação segue a arquitetura padrão de componentes Android, utilizando:
- **Mapas**: Exibição do mapa com base no SDK do Google Maps.
- **API de Clima**: Consome dados de uma API externa para mostrar as informações do clima.
- **Interface de Utilizador**: O layout foi projetado para ser simples e intuitivo, com uma tela principal exibindo o mapa e um botão para obter as informações do clima.

A arquitetura do projeto é organizada nas seguintes pastas:
- **app**: Contém a lógica da aplicação, incluindo as Activities, serviços e funções de back-end para obter os dados da API de Clima.
- **build.gradle.kts**: Configurações de build do Gradle, necessárias para compilar e executar a aplicação.
- **.gradle, .idea**: Arquivos de configuração do projeto no Android Studio.

## 4. Implementação

A implementação foi dividida nas seguintes partes:

### Configuração do Projeto
1. **Criação do Projeto Android**: O projeto foi configurado no Android Studio, com Kotlin como linguagem principal e as dependências necessárias (Google Maps, API de Clima) adicionadas no arquivo `build.gradle.kts`.
   
2. **Integração com o Google Maps**: A funcionalidade de mapas foi integrada utilizando a **API do Google Maps**, que permite ao utilizador tocar no mapa e selecionar um local.

3. **Integração com a API de Clima**: A aplicação consome uma API de Clima para obter dados meteorológicos em tempo real com base na localização selecionada no mapa.

4. **Exibição das Informações**: Após a seleção de um local no mapa, as informações do clima são exibidas na interface utilizando **TextViews** e **ImageViews** para mostrar a temperatura, condições do tempo e ícones representando o clima.

### Código Exemplo para Acessar a API de Clima:
```kotlin
val weatherApiUrl = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey"

val response = URL(weatherApiUrl).readText()
val weatherData = JSONObject(response)
val temperature = weatherData.getJSONObject("main").getDouble("temp")
```

## 5. Testes e Validação

### Estratégia de Testes:

*   **Testes Funcionais**: Foram realizados testes para garantir que a aplicação recupera as informações corretamente da API de Clima e exibe-as na interface de forma precisa.
    
*   **Testes no Emulador**: A aplicação foi testada no emulador **Pixel 9 Pro**, com diferentes locais sendo selecionados no mapa e as informações do clima sendo recuperadas com sucesso.
    
*   **Testes em Dispositivo Real**: A aplicação também foi testada em dispositivos reais, validando que a exibição do mapa e as informações de clima funcionam corretamente em diferentes tipos de dispositivos.

### Resultados Esperados:

A aplicação deve exibir as condições meteorológicas do local selecionado, incluindo:

*   Temperatura atual
    
*   Condições do tempo (ex.: ensolarado, chuvoso, nublado)
    
*   Outros detalhes como umidade, pressão e velocidade do vento.

## 6. Instruções de Uso

Para executar o projeto, siga os seguintes passos:

1.  **Instalar o Android Studio**: Baixe e instale o Android Studio no seu computador.
    
2.  **Abrir o Projeto**: Clone o repositório ou abra o projeto diretamente no Android Studio.
    
3.  **Configurar as Chaves de API**: Adicione suas próprias chaves de API do Google Maps e da API de Clima no arquivo `strings.xml`.
    
4.  **Executar a Aplicação**: Conecte um dispositivo Android ou use um emulador para rodar a aplicação.
    
5.  **Usar o Mapa**: Selecione um local no mapa para visualizar as condições meteorológicas.
