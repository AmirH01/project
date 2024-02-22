package com.example.mytempapplication

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ApiClient {

    private val client: HttpClient = HttpClient(Android){
        install(ContentNegotiation){
            json(Json {ignoreUnknownKeys = true})

        }
    }

    suspend fun get(medicine: String): APIMedicineResponse?{
        return try {
            val apiMedicineResponse: APIMedicineResponse  = client.get {
                url("${HttpRoutes.MEDICINES}/$medicine/")
                header("subscription-key", "44ca4e8e5d624e43a5cdc6f38a7f4f07")
                header("Accept", "application/json")
            }.body()
            return apiMedicineResponse
        }
        catch(e: ClientRequestException){
            Log.e("EXCEPTION", e.message)
            Log.e("ERROR CODE", e.response.status.toString())
            null
        }
    }




}
