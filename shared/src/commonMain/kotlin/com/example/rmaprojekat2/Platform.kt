package com.example.rmaprojekat2

interface Platform {
    val name: String
}

expect fun currentPlatform(): Platform

class GreetingHelper {
    fun greet(): String = "Hello, ${currentPlatform().name}!"
}