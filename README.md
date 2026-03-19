# Trabalho Prático 1 - Kotlin Básico e Introdução ao Mundo Android

**Curso**: Computação Móvel  
**Aluno**: Rafael Falcão
**Data**: 19/03/2026
**URL do Repositório**: https://github.com/rafa15022/Tutorial1/

---

## 1. Introdução
Este repositório contém as implementações do Trabalho Prático 1 para a disciplina de **Computação Móvel**. O objetivo do trabalho é introduzir o aluno ao desenvolvimento de **aplicações Android** utilizando a linguagem **Kotlin**. O trabalho abrange a criação de uma aplicação Android básica, bem como o desenvolvimento de uma calculadora simples em Kotlin, a modelagem de um comportamento físico (simulação de uma bola saltando) e a construção de apps completos como **WeatherApp** e **SystemInfoApp**.

---

## 2. Visão Geral do Sistema
O sistema desenvolvido no projeto é composto pelos seguintes componentes:

### Exercícios de Kotlin (IntelliJ IDEA):
1. **Exercício 1** - **Quadrados Perfeitos**: Cálculo dos quadrados perfeitos dos números de 1 a 50 usando três abordagens:
   - **IntArray**
   - **Range e Map()**
   - **Array com o Construtor**

2. **Exercício 2** - **Calculadora**: Implementação de uma calculadora simples com operações aritméticas e bitwise, com tratamento de exceções como divisão por zero.

3. **Exercício 3** - **Simulação de Bola Saltando**: Modelagem da altura de uma bola que começa a cair de uma altura de 60 metros, com a altura reduzindo **60%** a cada salto.

4. **Exercício 6** - **Biblioteca e Livros**: Implementação de um sistema simples de biblioteca com funcionalidades para:
- **Adicionar livros** (físicos e digitais).
- **Empréstimo e devolução de livros**.
- **Pesquisa de livros por autor**.

### Aplicações Android:
4. **WeatherApp**: Aplicação Android que mostra a previsão do tempo para uma cidade específica utilizando uma API externa de clima.
5. **SystemInfoApp**: Aplicação Android que exibe informações sobre o dispositivo, como modelo, sistema operacional e memória disponível.
6. **HelloWorld**: Aplicação Android simples que exibe a mensagem "Hello, World!" na tela.

---

## 3. Estrutura do Projeto
O projeto está dividido nas seguintes pastas, cada uma contendo um exercício específico ou aplicação:

- **exer_1**: Quadrados Perfeitos - Cálculo dos quadrados perfeitos de 1 a 50 usando diferentes abordagens (IntArray, Range, Array).
- **exer_2**: Calculadora - Implementação de uma calculadora com operações aritméticas e bitwise, com exceções tratadas.
- **exer_3**: Simulação de Bola Saltando - Cálculo da altura dos saltos de uma bola após cada queda.
- **exer_6**: **Biblioteca e Livros** - Implementação de um sistema simples de biblioteca com funcionalidades para adicionar livros (físicos e digitais), empréstimo e devolução de livros, e pesquisa por autor.
- **weather_app**: WeatherApp - Aplicação Android para exibir a previsão do tempo.
- **system_info_app**: SystemInfoApp - Aplicação Android que mostra informações do sistema.
- **hello_world**: HelloWorld - Aplicação Android simples que exibe uma mensagem na tela.

---

## 4. Como Executar o Projeto

### Requisitos:
- **Android Studio** instalado e configurado com o **SDK Kotlin**.
- **API Key** para o **WeatherApp** (se necessário).

### Configuração:
Clone o repositório e abra o projeto no **Android Studio**.

1. **Clone o Repositório**:
   ```bash
   git clone https://github.com/rafa15022/Tutorial1/
   ```
2. **Abra no Android Studio**:
Após clonar o repositório, abra o projeto no Android Studio para visualizar e executar as aplicações Android.

### Executando os Exercícios de Kotlin (IntelliJ IDEA):
- Para os exercícios como a calculadora e a simulação da bola saltando, você pode executar os arquivos Kotlin diretamente no IntelliJ IDEA.

## 5. Descrição dos Exercícios e Aplicações

### **Exercício 1** - **Quadrados Perfeitos**
O objetivo deste exercício é calcular os **quadrados perfeitos** dos números de 1 a 50 usando três abordagens:
- **IntArray**: Utiliza um array de inteiros para armazenar os quadrados perfeitos.
- **Range e Map()**: Usa o conceito de range e a função map() para gerar os quadrados perfeitos.
- **Array com o Construtor**: Criação de um array com o construtor e preenchimento dos valores dos quadrados perfeitos.

### **Exercício 2** - **Calculadora em Kotlin**
Este exercício implementa uma **calculadora** que permite realizar operações matemáticas com dois números inteiros. As operações disponíveis incluem:
- **Operações aritméticas**: Soma, subtração, multiplicação e divisão.
- **Operações lógicas**: AND, OR, NOT.
- **Operações bitwise**: shl (deslocamento à esquerda) e shr (deslocamento à direita).

### **Exercício 3** - **Simulação de Bola Saltando**
Neste exercício, foi modelado o comportamento de uma bola que salta, começando com uma altura de 60 metros e reduzindo sua altura **60%** após cada salto. A sequência é gerada utilizando a função **generateSequence**, e os saltos são filtrados para exibir apenas os que têm altura superior a 1 metro.

### **Exercício 6** - **Biblioteca e Livros**
Este exercício consiste na implementação de um sistema de **biblioteca** simples, com funcionalidades para adicionar livros, empréstimos, devoluções e pesquisa de livros por autor. O sistema foi construído utilizando **Kotlin** e a estrutura orientada a objetos, com o uso de classes para representar livros e operações da biblioteca.

#### **Classe Livro**
A **classe `Livro`** é a classe base, com as propriedades **titulo**, **autor**, **ano_publicacao** e **copias**.

#### **Subclasses de Livro**
1. **`Livro_Físico`**: Representa livros físicos, com propriedades adicionais, como o **peso** e se o livro tem **capa dura**.
2. **`Livro_Digital`**: Representa livros digitais, com informações adicionais sobre o **tamanho do arquivo** e o **formato**.

#### **Classe Biblioteca**
A **classe `Biblioteca`** gerencia uma lista de livros e oferece funcionalidades como:
- **Adicionar um livro**.
- **Empréstimo de livros**: Reduz a quantidade de cópias disponíveis de um livro.
- **Devolução de livros**: Aumenta a quantidade de cópias de um livro disponível.
- **Pesquisa por autor**: Permite pesquisar livros de um autor específico.

#### **Membro da Biblioteca**
A funcionalidade de **membros da biblioteca** foi implementada através da **data class `Membro_Biblioteca`**, que mantém informações sobre o nome do membro, o número de sócio e os livros que ele tem emprestados.

---

## 6. Testes e Validação

O projeto foi testado para garantir que:
- As operações na calculadora funcionem corretamente (incluindo o tratamento de erros, como divisão por zero).
- O modelo da bola saltando exiba corretamente as alturas até o limite de 1 metro.
- O layout da aplicação Android se adapte bem em diferentes tamanhos de tela, usando o **ConstraintLayout**.
- As aplicações Android interajam corretamente com as APIs externas (como a API de clima para o **WeatherApp**).

### Casos de teste:
- **Calculadora**: Testar a entrada de dados válidos e inválidos para todas as operações.
- **Bola a Saltar**: Validar se o cálculo das alturas segue a regra de 60% a cada salto.
- **WeatherApp**: Validar a comunicação com a API externa e o correto funcionamento da interface.
- **SystemInfoApp**: Verificar a exibição das informações corretas sobre o dispositivo.

---

## 8. Uso de IA (ChatGPT)

Durante o desenvolvimento do trabalho prático, foi utilizado o **ChatGPT** para ajudar na **criação da WeatherApp** e na geração dos **arquivos README.md**. O restante do código e implementações foram feitas manualmente.

---

### Links Importantes:
- **WeatherApp**: [README do WeatherApp](https://github.com/rafa15022/Tutorial1/blob/main/WeatherApp/README.md)
- **SystemInfoApp**: [README do SystemInfoApp](https://github.com/rafa15022/Tutorial1/blob/main/SystemInfoApp/README.md)
- **HelloWorld**: [README do HelloWorld](https://github.com/rafa15022/Tutorial1/blob/main/15022/HelloWorld/README.md)
