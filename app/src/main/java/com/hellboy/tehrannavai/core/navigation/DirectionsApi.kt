package com.hellboy.tehrannavai.core.navigation

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// مدل‌های پاسخ گوگل Directions API
data class DirectionsResponse(val routes: List<Route>)
data class Route(val legs: List<Leg>, val overview_polyline: Polyline)
data class Leg(val distance: TextValue, val duration: TextValue, val steps: List<Step>)
data class Step(val end_location: Location, val html_instructions: String)
data class TextValue(val text: String, val value: Int)
data class Polyline(val points: String)
data class Location(val lat: Double, val lng: Double)

interface DirectionsApiService {
    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String, // "lat,lng"
        @Query("destination") destination: String, // "lat,lng" یا نام مکان
        @Query("key") apiKey: String,
        @Query("language") language: String = "fa"
    ): DirectionsResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://maps.googleapis.com/"
    
    val directionsApi: DirectionsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DirectionsApiService::class.java)
    }
}
