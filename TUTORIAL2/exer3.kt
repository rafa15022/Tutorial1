package org.example.cm.exer3

// Classe Pipeline com lista ordenada de stages, cada um com nome e função de transformação
class Pipeline {
    private val stages = mutableListOf<Pair<String, (List<String>) -> List<String>>>()

    // AddStage acrescenta um stage com nome e função de transformação à pipeline
    fun addStage(name: String, transform: (List<String>) -> List<String>) {
        stages.add(Pair(name, transform))
    }

    // Execute corre o input por todos os stages em ordem e devolve o resultado final
    fun execute(input: List<String>): List<String> {
        var result = input
        for (stage in stages) {
            result = stage.second(result)
        }
        return result
    }

    // Classe Pipeline com lista ordenada de stages, cada um com nome e função de transformação
    class Pipeline {

        private val stages = mutableListOf<Pair<String, (List<String>) -> List<String>>>()

        // AddStage acrescenta um stage com nome e função de transformação à pipeline
        fun addStage(name: String, transform: (List<String>) -> List<String>) {
            stages.add(Pair(name, transform))
        }

        // Execute corre o input por todos os stages em ordem e devolve o resultado final
        fun execute(input: List<String>): List<String> {
            var result = input
            for (stage in stages) {
                result = stage.second(result)
            }
            return result
        }

        // Describe imprime o nome de cada stage em ordem, conforme pedido pelo professor
        fun describe() {
            println("Pipeline stages:")
            var i = 1
            for (stage in stages) {
                println("$i. ${stage.first}")
                i = i + 1
            }
        }

        // Compose junta dois stages existentes num único novo stage usando composição de funções
        fun compose(name1: String, name2: String) {
            var stage1: ((List<String>) -> List<String>)? = null
            var stage2: ((List<String>) -> List<String>)? = null

            for (stage in stages) {
                if (stage.first == name1) stage1 = stage.second
                if (stage.first == name2) stage2 = stage.second
            }

            if (stage1 != null && stage2 != null) {
                val composed: (List<String>) -> List<String> = { input ->
                    stage2(stage1(input))
                }
                addStage("$name1+$name2", composed)
            }
        }

        // Fork corre o mesmo input em dois pipelines independentes e devolve um par com ambos os resultados
        fun fork(other: Pipeline, input: List<String>): Pair<List<String>, List<String>> {
            val result1 = this.execute(input)
            val result2 = other.execute(input)
            return Pair(result1, result2)
        }
    }

    // buildPipeline é uma função top-level que aceita um lambda com receiver Pipeline
    fun buildPipeline(block: Pipeline.() -> Unit): Pipeline {
        val pipeline = Pipeline()
        pipeline.block()
        return pipeline
    }

    fun main() {

        val logs = listOf(
            "  INFO: server started  ",
            "  ERROR: disk full  ",
            "  DEBUG: checking config  ",
            "  ERROR: out of memory  ",
            "  INFO: request received  ",
            "  ERROR: connection timeout  "
        )

        // Construção da pipeline com os 4 stages: Trim, Filter errors, Uppercase e Add index
        val pipeline = buildPipeline {

            addStage("Trim") { lines ->
                val result = mutableListOf<String>()
                for (line in lines) {
                    result.add(line.trim())
                }
                result
            }

            addStage("Filter errors") { lines ->
                val result = mutableListOf<String>()
                for (line in lines) {
                    if (line.contains("ERROR")) {
                        result.add(line)
                    }
                }
                result
            }

            addStage("Uppercase") { lines ->
                val result = mutableListOf<String>()
                for (line in lines) {
                    result.add(line.uppercase())
                }
                result
            }

            addStage("Add index") { lines ->
                val result = mutableListOf<String>()
                var i = 1
                for (line in lines) {
                    result.add("$i. $line")
                    i = i + 1
                }
                result
            }
        }

        // Descrição e execução da pipeline
        pipeline.describe()

        println("\nResult:")
        val output = pipeline.execute(logs)
        for (line in output) {
            println(line)
        }
    }

    // Describe imprime o nome de cada stage em ordem, conforme pedido pelo professor
    fun describe() {
        println("Pipeline stages:")
        var i = 1
        for (stage in stages) {
            println("$i. ${stage.first}")
            i = i + 1
        }
    }

    // Compose junta dois stages existentes num único novo stage usando composição de funções
    fun compose(name1: String, name2: String) {
        var stage1: ((List<String>) -> List<String>)? = null
        var stage2: ((List<String>) -> List<String>)? = null

        for (stage in stages) {
            if (stage.first == name1) stage1 = stage.second
            if (stage.first == name2) stage2 = stage.second
        }

        if (stage1 != null && stage2 != null) {
            val composed: (List<String>) -> List<String> = { input ->
                stage2(stage1(input))
            }
            addStage("$name1+$name2", composed)
        }
    }

    // Fork corre o mesmo input em dois pipelines independentes e devolve um par com ambos os resultados
    fun fork(other: Pipeline, input: List<String>): Pair<List<String>, List<String>> {
        val result1 = this.execute(input)
        val result2 = other.execute(input)
        return Pair(result1, result2)
    }
}

// buildPipeline é uma função top-level que aceita um lambda com receiver Pipeline
fun buildPipeline(block: Pipeline.() -> Unit): Pipeline {
    val pipeline = Pipeline()
    pipeline.block()
    return pipeline
}

fun main() {

    val logs = listOf(
        "  INFO: server started  ",
        "  ERROR: disk full  ",
        "  DEBUG: checking config  ",
        "  ERROR: out of memory  ",
        "  INFO: request received  ",
        "  ERROR: connection timeout  "
    )

    // Construção da pipeline com os 4 stages: Trim, Filter errors, Uppercase e Add index
    val pipeline = buildPipeline {

        addStage("Trim") { lines ->
            val result = mutableListOf<String>()
            for (line in lines) {
                result.add(line.trim())
            }
            result
        }

        addStage("Filter errors") { lines ->
            val result = mutableListOf<String>()
            for (line in lines) {
                if (line.contains("ERROR")) {
                    result.add(line)
                }
            }
            result
        }

        addStage("Uppercase") { lines ->
            val result = mutableListOf<String>()
            for (line in lines) {
                result.add(line.uppercase())
            }
            result
        }

        addStage("Add index") { lines ->
            val result = mutableListOf<String>()
            var i = 1
            for (line in lines) {
                result.add("$i. $line")
                i = i + 1
            }
            result
        }
    }

    // Descrição e execução da pipeline
    pipeline.describe()

    println("\nResult:")
    val output = pipeline.execute(logs)
    for (line in output) {
        println(line)
    }
}