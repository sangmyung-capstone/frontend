package com.bapool.bapool.retrofit.data

//data class GetRestaurantsResult(
//    val code: String,
//    val message: String,
//    val result: List<Restaurant>
//)
//data class Restaurant(
//    val address_name: String,
//    val category_group_code: String,
//    val category_group_name: String,
//    val category_name: String,
//    val distance: String,
//    val id: String,
//    val phone: String,
//    val place_name: String,
//    val place_url: String,
//    val road_address_name: String,
//    val x: String,
//    val y: String
//)


data class GetRestaurantsResult(
    val code: Int,
    val message: String,
    val result: RestaurantsResult
)
data class RestaurantsResult(
    val restaurants: List<Restaurant>
)
data class Restaurant(
    val restaurant_id: Long,
    val restaurant_name: String,
    val restaurant_address: String,
    val category: String,
//    val imgUrl: String,   // 구현 방식 변경으로 imgUrl 관련 추가 api 생성 예정
    val num_of_party: Int,
    val restaurant_longitude: Double,
    val restaurant_latitude: Double
)


data class GetRestaurantLogResponse(
    val code: Int,
    val message: String,
    val result: Result
) {
    data class Result(
        val parties: List<Party>
    )

    data class Party(
        val party_id: Int,
        val party_name: String,
        val restaurant_name: String,
        val imgUrl: String,
        val restaurant_address: String,
        val category: String
    )
}



