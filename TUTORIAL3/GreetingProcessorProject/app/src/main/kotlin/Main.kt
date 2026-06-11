package com.dam

fun main() {
    val input = "Name: John Address: 123 Street"

    val extractor = DataProcessorExtractor(input)

    println("Name: ${extractor.getName()}")
    println("Address: ${extractor.getAddress()}")
}