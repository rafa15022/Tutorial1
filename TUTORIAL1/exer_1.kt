package org.example.cm.exer_1

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    // a) IntArray
    val a = IntArray(50)
    for (i in 0 until 50) {
        a[i] = (i+1) * (i+1)
    }
    // b) Range e Map()
    val b = (1..50).map{it * it}

    // c) Array com o construtor
    val c = Array(50) {0} // todas as posições inicializam-se com o valor 0
    for (i in 0 until 50) {
        c[i] = (i+1) * (i+1)
    }
    // Saída dos dados
    println("| Quadrados Perfeitos")
    println("IntArray: $a.joinToString()")
    println("Range e Map(): $b")
    println("Array com o Construtor: $c.joinToString()")
}