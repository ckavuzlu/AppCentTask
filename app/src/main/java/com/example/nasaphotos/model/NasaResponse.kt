package com.example.nasaphotos.model

import com.google.gson.annotations.SerializedName

data class NasaResponse(

	@SerializedName("photos")
	val photos: List<PhotosItem?>? = null
)
