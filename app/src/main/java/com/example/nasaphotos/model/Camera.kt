package com.example.nasaphotos.model

import com.google.gson.annotations.SerializedName

data class Camera(

	@SerializedName("full_name")
	val fullName: String? = null,

	@SerializedName("name")
	val name: String? = null,

	@SerializedName("id")
	val id: Int? = null,

	@SerializedName("rover_id")
	val roverId: Int? = null
)



