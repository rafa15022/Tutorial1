package org.example.cm.exer_vl

abstract class Livro (val titulo:String, val autor:String, private val ano_publicacao:Int, copias_iniciais:Int) {
    val classificacao: String
        get() { // O custom getter como pedido
            return if (ano_publicacao < 1980) {
                "Clássico"
            }
            else if (ano_publicacao <= 2010) {
                "Moderno"
            }
            else {
                "Contemporâneo"
            }
        }
    // Inicializar as copias disponiveis com o mesmo valor das copias iniciais
    var copias_disponiveis: Int = copias_iniciais

        set(value) {
            if (value < 0) { // Para não permitir valores negativos
                println("Número de copias inválido.")
            }
            else {
                field = value
            }
        }

    init { // O bloco init que imprime quando um livro é criado
        println("Livro '$titulo' de $autor, foi adicionado à biblioteca.")
    }

    abstract fun obter_info_armazenamento(): String

    override fun toString(): String {
        val texto_copias = if (copias_disponiveis == 1) {
            "Copia"
        }
        else {
            "Copias"
        }
        return "Título: $titulo, Autor: $autor, Classificação: $classificacao, Disponível: $copias_disponiveis $texto_copias"
    }
}

class Livro_Digital(titulo:String, autor:String, ano_publicacao:Int, copias:Int, val tamanho_ficheiro:Double, val formato:String):
    // O construtor que chama o pai construtor
    Livro(titulo, autor, ano_publicacao, copias) {
    override fun obter_info_armazenamento(): String {
        // Inclui o tamanho em mb e o formato, conforme pedido
        return "Armazenado digitalmente: $tamanho_ficheiro MB, Formato: $formato"
    }
}
                                                                                              // Variável que guarda se o livro tem ou não capa dura, inicializada como verdadeiro
class Livro_Fisico(titulo:String, autor:String, ano_publicacao:Int, copias:Int, val peso:Int, val tem_capa_dura: Boolean=true):
    // O construtor que chama o pai construtor
    Livro(titulo, autor, ano_publicacao, copias) {
    override fun obter_info_armazenamento(): String {
        if (tem_capa_dura) { // Mostra a propriedade nova: peso e também se o livro tem ou não capa dura
            return "Livro Físico: ${peso}g, Capa Dura: Sim"
        }
        else {
            return "Livro Físico: ${peso}g, Capa Dura: Não"
        }
    }
}

class Biblioteca (val nome:String) {
    var livros = mutableListOf<Livro>() // Guarda lista de livros

    // Adiciona um objeto companion à biblioteca que analisa:
    companion object {

        // total de livros adicionados em todas as instâncias biblioteca
        var total = 0

        // Retorna a quantidade de livros criados
        fun obter_total_livros_criados(): Int {
            return total
        }
    }

    // Implementa o metodo de adicionar livro, conforme pedido
    fun adicionar_livro(livro: Livro) {
        livros.add(livro)
        total++
    }

    // Implementa o metodo de pedir emprestado um livro, conforme pedido
    fun pedir_livro_emprestado(titulo: String) {
        var encontrou = false

        for (livro in livros) {
            if (livro.titulo == titulo) { // Pesquisa livro pelo titulo
                encontrou = true

                if (livro.copias_disponiveis > 0) {
                    livro.copias_disponiveis-- // Se o livro tiver copias, diminui as mesmas por 1

                    // Imprime uma mensagem apropriada caso seja sucedido
                    println("'$titulo' emprestado com sucesso. ${livro.copias_disponiveis} copias disponíveis.")

                    // C
                    if (livro.copias_disponiveis == 0) { // Imprime uma mensagem, no caso de já não haver copias
                        println("Aviso: Livro está esgotado! ")
                    }

                } else {
                    println("Desculpe, não existem mais copias disponíveis de '$titulo'.")
                }
            }
        }
        if (encontrou == false) {
            println("Desculpe, livro não encontrado.")
        }
    }

    // Implementa método de devolução de livro
    fun devolucao_livro(titulo: String) {
        var encontrou = false

        for (livro in livros) {
            if (livro.titulo == titulo) { // Pesquisa livro pelo titulo
                livro.copias_disponiveis++ // Se o livro for encontrado, adiciona-se uma copia

                // Imprime mensagem de confirmação
                println("Livro '$titulo' devolvido com sucesso. ${livro.copias_disponiveis} copias disponiveis.")
                encontrou = true
            }
        }

        if (encontrou == false) {
            println("Livro não encontrado.")
        }
    }

    // Implementa metodo de mostrar livros
    fun mostrar_livros() {
        // Imprime detalhes dos livros da biblioteca
        println("\n| Catálogo da Biblioteca: ")
        for (livro in livros) {
            println(livro.toString())
            println(" Armazenamento: ${livro.obter_info_armazenamento()} \n")
        }
    }

    // Implementa metodo de pesquisar por autor
    fun pesquisar_por_autor(autor: String) {
        var existe = false
        println("Livros de $autor: ")

        for (livro in livros) {
            if (livro.autor == autor) {
                existe = true
                println("- ${livro.titulo} (${livro.classificacao}, ${livro.copias_disponiveis} copias disponiveis)")
            }
        }

        if (existe == false) {
                println("Não foram encontrados quaisqueres livros.")
        }

    }
    // Implementa uma data class Membro_Biblioteca com as propriedades: nome, numero_socio, livros_emprestados
    data class Membro_Biblioteca(val nome: String, val numero_socio: Int, val livros_emprestados: MutableList<String>)
}
    fun main() {
        val biblioteca = Biblioteca("Biblioteca Central")

        val livro_digital = Livro_Digital("Kotlin in Action", "Dmitry Jemerov", 2017, 5, 4.5, "PDF")
        val livro_fisico = Livro_Fisico("Clean Code", "Robert C. Martin", 2008, 3, 650, true)
        val livro_classico = Livro_Fisico("1984", "George Orwell", 1949, 2, 400, false)

        biblioteca.adicionar_livro(livro_digital)
        biblioteca.adicionar_livro(livro_fisico)
        biblioteca.adicionar_livro(livro_classico)

        biblioteca.mostrar_livros()

        println("| Livros em Empréstimo: ")
        biblioteca.pedir_livro_emprestado("Clean Code")
        biblioteca.pedir_livro_emprestado("1984")
        biblioteca.pedir_livro_emprestado("1984")
        biblioteca.pedir_livro_emprestado("1984") // Deve falhar - sem copias disponiveis

        println("\n| Livro a Retornar: ")
        biblioteca.devolucao_livro("1984")

        println("\n| Procurar por Autor: ")
        biblioteca.pesquisar_por_autor("George Orwell")
    }



