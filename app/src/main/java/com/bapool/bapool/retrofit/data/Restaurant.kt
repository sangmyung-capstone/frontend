package com.bapool.bapool.retrofit.data

data class GetRestaurantsResult(
    val code: String,
    val message: String,
    val body: List<Restaurant>
)

data class Restaurant(
    val id: Int,
    val name: String,
    val address: String,
    val category: String,
    val imgUrl: String,
    val num_of_group: Int,
    val res_x: Double,
    val res_y: Double
)
