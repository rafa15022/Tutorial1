# Assignment 1 – WeatherApp: Aplicação Meteorológica com Mapa
Curso: Computação Móvel  
Aluno: Rafael Falcão
Data: 19/03/2026
URL do Repositório: https://github.com/rafa15022/Tutorial1

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
- **Testes de Funcionalidade**: A principal funcionalidade testada foi a exibição das informações corretas do dispositivo. Foram realizados testes em dispositivos e emuladores para verificar a precisão das informações exibidas.
  
- **Testes no Emulador**: O emulador Pixel 9 Pro foi utilizado para simular o dispositivo Android e validar a exibição das informações. Todos os dados (modelo, fabricante, versão Android, etc.) foram mostrados corretamente.

- **Testes em Dispositivo Real**: A aplicação também foi testada em um dispositivo físico, garantindo que o comportamento fosse o mesmo.

### Resultados Esperados:
A aplicação deve exibir as seguintes informações no **TextView**:
- Modelo do dispositivo
- Fabricante
- Versão do Android
- Número de série

## 6. Instruções de Uso

Para executar o projeto, siga os seguintes passos:

1. **Instalar o Android Studio**: Baixe e instale o Android Studio no seu computador.
2. **Abrir o Projeto**: Clone o repositório ou abra o projeto diretamente no Android Studio.
3. **Executar a Aplicação**: Conecte um dispositivo Android ou use um emulador para rodar a aplicação.
4. **Exibir a Mensagem**: Após executar o aplicativo, a mensagem "Hello, World!" será exibida na tela.
