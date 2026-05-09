package org.example.cm.exer1

// Define-se aqui a sealed class Event com as subclasses Login, Purchase e Logout
sealed class Event {
    data class Login(val username: String, val timestamp: Long) : Event()
    data class Purchase(val username: String, val amount: Double, val timestamp: Long) : Event()
    data class Logout(val username: String, val timestamp: Long) : Event()
}

// Extension function filterByUser que filtra os eventos de um utilizador específico
fun List<Event>.filterByUser(username: String): List<Event> {
    val result = mutableListOf<Event>()
    for (event in this) {
        if (event is Event.Login && event.username == username) {
            result.add(event)
        }
        if (event is Event.Purchase && event.username == username){
            result.add(event)
        }
        if (event is Event.Logout && event.username == username){
            result.add(event)
        }
    }
    return result
}

// Extension function totalSpent que soma o total gasto por um utilizador
fun List<Event>.totalSpent(username: String): Double {
    var total = 0.0
    for (event in this) {
        if (event is Event.Purchase && event.username == username) {
            total = total + event.amount
        }
    }
    return total


}

//  Higher-order function processEvents que aplica um handler a cada evento da lista
fun processEvents(events: List<Event>, handler: (Event) -> Unit) {
    for (event in events) {
        handler(event)
    }
}

fun main() {
    val events = listOf(
        Event.Login("alice", 1_000),
        Event.Purchase("alice", 49.99, 1_100),
        Event.Purchase("bob", 19.99, 1_200),
        Event.Login("bob", 1_050),
        Event.Purchase("alice", 15.00, 1_300),
        Event.Logout("alice", 1_400),
        Event.Logout("bob", 1_500)
    )

    // Expressão when dentro do lambda passado ao processEvents, com todos os ramos tratados
    processEvents(events) { event ->
        when (event) {
            is Event.Login -> println("[LOGIN] ${event.username} logged in at t=${event.timestamp}")
            is Event.Purchase -> println("[PURCHASE] ${event.username} spent $${event.amount} at t=${event.timestamp}")
            is Event.Logout -> println("[LOGOUT] ${event.username} logged out at t=${event.timestamp}")
        }
    }

    // Teste com totalSpent e filterByUser para verificar o output esperado
    println("Total spent by alice: $${events.totalSpent("alice")}")
    println("Total spent by bob: $${events.totalSpent("bob")}")

    println("Events for alice:")
    for (event in events.filterByUser("alice")) {
        println(event)
    }
}