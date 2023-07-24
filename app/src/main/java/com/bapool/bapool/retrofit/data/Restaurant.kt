package com.bapool.bapool.retrofit.data

import java.io.Serializable

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
    val restaurant_latitude: Double,
    val link: String
)
// -------------------------------------------------------------------------------------------------
data class GetRestaurantLogResponse(
    val code: Int,
    val message: String,
    val result: Result
) {
    data class Result(
        val partyInfoList: List<Party>
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
// -------------------------------------------------------------------------------------------------
data class GetRestaurantInfoResult(
    val code: Int,
    val message: String,
    val result: RestaurantInfo
)
data class RestaurantInfo(
    val restaurant_id: Long,
    val restaurant_name: String,
    val restaurant_longitude: Double,
    val restaurant_latitude: Double,
    val restaurant_address: String,
    val num_of_party: Int,
    val category: String,
    val link: String,
    val phone: String,
    val img_url: String,
    val menu: List<Menu>
)
data class Menu(
    val name: String,
    val price: String
)
// -------------------------------------------------------------------------------------------------
data class GetRestaurantsBottomRequest(
    val restaurant_ids: List<Long>
)
data class GetRestaurantsBottomResult(
    val code: Int,
    val message: String,
    val result: RestaurantsBottomResult
)
data class RestaurantsBottomResult(
    val restaurant_img_urls: List<String>
)
// -------------------------------------------------------------------------------------------------
data class GetSearchResult(
    val code: Int,
    val message: String,
    val result: SearchResult
)
data class SearchResult(
    val restaurants: List<RestaurantSearch>
)
data class RestaurantSearch(
    val category: String,
    val num_of_party: Int,
    val restaurant_address: String,
    val restaurant_id: Long,
    val restaurant_name: String,
    val restaurant_longitude: Double,
    val restaurant_latitude: Double
)



// -------------------------------------------------------------------------------------------------
//식당정보  ->  식당안의 파티리스트로 넘기는 intent 객체


data class goToRestaurantPartyList(
    val restaurant_id: Long?,
    val name: String?,
    val address: String?,
    val img_url: String,
    val site_url: String,
    val category: String,
    val phone: String,
): Serializable
