package com.safety.app.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface DirectionsApiService {
    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") apiKey: String,
        @Query("departure_time") departureTime: String = "now",
        @Query("traffic_model") trafficModel: String = "best_guess"
    ): DirectionsResponse

    companion object {
        private const val BASE_URL = "https://maps.googleapis.com/"

        fun create(): DirectionsApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DirectionsApiService::class.java)
        }
    }
}

data class DirectionsResponse(
    val routes: List<Route>,
    val status: String
)

data class Route(
    val overview_polyline: Polyline,
    val legs: List<Leg>
)

data class Polyline(
    val points: String
)

data class Leg(
    val distance: TextValue,
    val duration: TextValue,
    val duration_in_traffic: TextValue?,
    val start_location: Location,
    val end_location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class TextValue(
    val text: String,
    val value: Long
)
