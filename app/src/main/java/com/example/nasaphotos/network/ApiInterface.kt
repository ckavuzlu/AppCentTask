package com.example.nasaphotos.network

import com.example.nasaphotos.model.NasaResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @GET("{roverType}/photos")
    fun getRoverPhotos(@Path("roverType") roverType: String,
                       @Query("sol") sol: Int,
                       @Query("page") page: Int?,
                       @Query("api_key") apiKey: String,
                       @Query("camera") camera: String? = null
    ) : Call<NasaResponse>
}