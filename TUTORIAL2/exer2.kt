package org.example.cm.exer2

// 1. Classe genérica Cache<K, V>
class Cache<K : Any, V : Any> {
    private val map = HashMap<K, V>()

    // Funções put, get, evict e size para gerir as entradas da cache
    // inserir ou sobrescrever
    fun put(key: K, value: V) {
        map[key] = value
    }
    // obter valor por chave, null se não existir
    fun get(key: K): V? {
        return map[key]
    }

    // remover entrada
    fun evict(key: K) {
        map.remove(key)
    }
    // número de entradas
    fun size(): Int {
        return map.size
    }
    // 2. getOrPut: devolve o valor se existir, senão calcula, guarda e devolve
    fun getOrPut(key: K, default: () -> V): V {
        if (map[key] != null) {
            return map[key]!!
        }
        val value = default()
        map[key] = value
        return value
    }

    // Transform aplica uma função ao valor existente, atualiza-o e devolve true, ou false se a chave não existir
    fun transform(key: K, action: (V) -> V): Boolean {
        if (map[key] == null) {
            return false
        }
        val oldValue = map[key]!!
        val newValue = action(oldValue)
        map[key] = newValue
        return true
    }

    // Snapshot devolve uma cópia imutável do conteúdo actual da cache
    fun snapshot(): Map<K, V> {
        return HashMap(map)
    }
    // FilterValues devolve um mapa imutável com apenas as entradas cujos valores satisfazem o predicado
    fun filterValues(predicate: (V) -> Boolean): Map<K, V> {
        val result = HashMap<K, V>()
        for (entry in map) {
            if (predicate(entry.value)) {
                result[entry.key] = entry.value
            }
        }
        return result
    }
}

fun main() {

    // Teste com Cache<String, Int> para contagem de frequência de palavras
    println("--- Word frequency cache ---")
    val wordCache = Cache<String, Int>()
    wordCache.put("kotlin", 1)
    wordCache.put("scala", 1)
    wordCache.put("haskell", 1)

    println("Size: ${wordCache.size()}")
    println("Frequency of \"kotlin\": ${wordCache.get("kotlin")}")

    println("getOrPut \"kotlin\": ${wordCache.getOrPut("kotlin") { 0 }}")
    println("getOrPut \"java\": ${wordCache.getOrPut("java") { 0 }}")
    println("Size after getOrPut: ${wordCache.size()}")

    println("Transform \"kotlin\" (+1): ${wordCache.transform("kotlin") { it + 1 }}")
    println("Transform \"cobol\" (+1): ${wordCache.transform("cobol") { it + 1 }}")

    println("Snapshot: ${wordCache.snapshot()}")

    val nonZero = wordCache.filterValues { it > 0 }
    println("Words with count > 0: $nonZero")

    // Teste com Cache<Int, String> para registo id -> nome
    println("\n--- Id registry cache ---")
    val idCache = Cache<Int, String>()
    idCache.put(1, "Alice")
    idCache.put(2, "Bob")

    println("Id 1 -> ${idCache.get(1)}")
    println("Id 2 -> ${idCache.get(2)}")

    idCache.evict(1)
    println("After evict id 1, size: ${idCache.size()}")
    println("Id 1 after evict -> ${idCache.get(1)}")
}