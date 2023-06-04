package com.bapool.bapool.retrofit.data

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

data class GetRestaurantInfoResult(
    val code: Int,
    val message: String,
    val result: RestaurantInfo
)
data class RestaurantInfo(
    val category: String,
    val imgUrl: String,
    val link: String,
    val menu: List<Menu>,
    val num_of_party: Int,
    val phone: String,
    val restaurant_address: String,
    val restaurant_id: Long,
    val restaurant_latitude: Double,
    val restaurant_longitude: Double,
    val restaurant_name: String
)
data class Menu(
    val name: String,
    val price: String
)



