package org.example.cm.exer4
import kotlin.math.sqrt

// Define-se aqui a data class Vec2 com as duas propriedades Double x e y
data class Vec2(val x: Double, val y: Double) : Comparable<Vec2> {

    // Sobrecarga dos operadores aritméticos entre dois Vec2
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun times(scalar: Double) = Vec2(x * scalar, y * scalar)
    operator fun unaryMinus() = Vec2(-x, -y)

    // Sobrecarga do compareTo por magnitude (permite <, >, <=, >= e max()/min() em listas)
    override fun compareTo(other: Vec2): Int =
        magnitude().compareTo(other.magnitude())

    // Sobrecarga do operador get, v[0] devolve x e v[1] devolve y
    operator fun get(index: Int): Double = when (index) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException("Vec2 só tem índices 0 e 1, recebeu $index")
    }

    // Ponto 4: Funções membro magnitude, dot e normalized
    fun magnitude(): Double = sqrt(x * x + y * y)

    fun dot(other: Vec2): Double = x * other.x + y * other.y

    fun normalized(): Vec2 {
        val m = magnitude()
        check(m != 0.0) { "Não é possível normalizar o vetor nulo" }
        return Vec2(x / m, y / m)
    }

}

// Multiplicação escalar pelo lado esquerdo (2.0 * v) implementada como extension function em Double
operator fun Double.times(v: Vec2) = Vec2(this * v.x, this * v.y)


fun main() {
    val a = Vec2(3.0, 4.0)
    val b = Vec2(1.0, 2.0)

    println("a = $a")
    println("b = $b")
    println("a + b = ${a + b}")
    println("a - b = ${a - b}")
    println("a * 2.0 = ${a * 2.0}")
    println("-a = ${-a}")
    println("|a| = ${a.magnitude()}")
    println("a dot b = ${a.dot(b)}")
    println("norm(a) = ${a.normalized()}")
    println("a[0] = ${a[0]}")
    println("a[1] = ${a[1]}")
    println("a > b = ${a > b}")
    println("a < b = ${a < b}")

    val vectors = listOf(Vec2(1.0, 0.0), Vec2(3.0, 4.0), Vec2(0.0, 2.0))
    println("Longest  = ${vectors.max()}")
    println("Shortest = ${vectors.min()}")

    println()
    println("── Challenges ──────────────────")

    println("2.0 * a = ${2.0 * a}")

    val (px, py) = a
    println("destructuring a → x=$px, y=$py")
}