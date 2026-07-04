package com.example.eventplanner.network

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

@Serializable
data class NWSPointsResponse(
    val properties: NWSPointsProperties
)

@Serializable
data class NWSPointsProperties(
    val forecast: String
)

@Serializable
data class NWSForecastResponse(
    val properties: NWSForecastProperties
)

@Serializable
data class NWSForecastProperties(
    val periods: List<NWSForecastPeriod>
)

@Serializable
data class NWSForecastPeriod(
    val name: String,
    val temperature: Int,
    val temperatureUnit: String,
    val shortForecast: String,
    val icon: String
)

interface WeatherApiService {
    @GET("points/{latitude},{longitude}")
    suspend fun getPointsMetadata(
        @Path("latitude") lat: Double,
        @Path("longitude") lon: Double
    ): NWSPointsResponse

    @GET
    suspend fun getForecast(
        @Url forecastUrl: String
    ): NWSForecastResponse
}