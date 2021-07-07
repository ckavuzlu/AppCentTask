package com.example.nasaphotos.model

import com.google.gson.annotations.SerializedName


data class PhotosItem(

    @SerializedName("sol")
    val sol: Int? = null,

    @SerializedName("earth_date")
    val earthDate: String? = null,

    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("camera")
    val camera: Camera? = null,

    @SerializedName("rover")
    val rover: Rover? = null,

    @field:SerializedName("img_src")
    val imgSrc: String? = null
)