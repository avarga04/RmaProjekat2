package com.example.rmaprojekat2

class JvmPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun currentPlatform(): Platform = JvmPlatform()