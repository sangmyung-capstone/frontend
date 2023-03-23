package com.bapool.bapool.retrofit.data

data class GetRestaurantsResult(
    val code: String,
    val message: String,
    val body: List<Restaurant>
)

data class Restaurant(
//    val id: Int,
//    val name: String,
//    val address: String,
//    val category: String,
//    val imgUrl: String,
//    val num_of_group: Int,
//    val res_x: Double,
//    val res_y: Double
    val address_name: String,
    val category_group_code: String,
    val category_group_name: String,
    val category_name: String,
    val distance: String,
    val id: String,
    val phone: String,
    val place_name: String,
    val place_url: String,
    val road_address_name: String,
    val x: String,
    val y: String
)
