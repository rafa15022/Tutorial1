package org.example.cm.exer_3

fun main() {
    // Aqui generei uma sequência onde começa com um valor inicial de 60.0
    val ressaltos = generateSequence(60.0) {i -> i * 0.6}
        .take(15) // Apenas pegar nos primeiros 15 ressaltos
        .filter {i -> i >= 1.0} // Filtrei os ressaltos para os que têm altura maior ou igual a 1.0m
        .toList() // Transformei em uma lista

    println("| Sequência dos ressaltos: ")

    for (i in ressaltos.indices) {
        println("${i+1}º -> ${"%.2f".format(ressaltos[i])}m")
    }
}