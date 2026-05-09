package org.example.cm.exer_2

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
fun main() {
    try { // Tenta fazer isto
        print("Escreva o primeiro valor: ")
        val primeiro = readln().toInt()

        print("Escolha uma das operações a seguir -> +, -, *, /, &&, ||, !, shl, shr: ")
        val operacao = readln()
        var segundo = 0

        var resultado = 0
        if (operacao != "!") {
            print("Escreva o segundo valor: ")
            segundo = readln().toInt()
        }

        when (operacao) {
            "+" -> resultado = primeiro + segundo
            "-" -> resultado = primeiro - segundo
            "*" -> resultado = primeiro * segundo
            "/" -> {
                if (segundo != 0) {
                    resultado = primeiro / segundo
                }
                else {
                    println("Erro: Nenhum número é divisivel por 0!")
                    return
                }
            }
            "&&" -> {
                if ((primeiro != 0) && (segundo != 0)) {
                    resultado = 1
                }
            }
            "||" -> {
                if ((primeiro != 0) || (segundo != 0)) {
                    resultado = 1
                }
            }
            "!" -> {
                if (primeiro == 0) {
                    resultado = 1
                }
                else {
                    resultado = 0
                }
            }
            "shl" -> resultado = primeiro shl segundo
            "shr" -> resultado = primeiro shr segundo
            else -> {
                println("Operação inválida")
                return
            }
        }

        println("| Resultado:")
        println("Decimal: $resultado")
        println("Hexadecimal ${resultado.toString(16)}")
        println("Booleano: ${resultado != 0}")
    }
    catch (e: Exception) { // Se ocorrer um erro ao tentar
        println("Erro: $e") // Mostra o mesmo
    }
}
