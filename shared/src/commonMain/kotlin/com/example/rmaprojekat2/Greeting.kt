package com.example.rmaprojekat2

class Greeting {
    private val platform = currentPlatform();

    fun greet(): String {
        return sayHello(platform.name)
    }
}