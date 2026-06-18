package com.example.rmaprojekat2.data.remote

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST


interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponseDto

    @POST("auth/signup")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): RegisterResponseDto

    @POST("auth/logout")
    suspend fun logout()
}