package com.example.app

import annotations.Greeting

open class MyClass {

    @Greeting("Hello from MyClass!")
    fun sayHello() {
        println("Executing sayHello method")
    }

    @Greeting("Welcome to the compute function!")
    fun compute() {
        println("Computing something important...")
    }
}