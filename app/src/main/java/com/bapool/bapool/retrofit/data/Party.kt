package com.bapool.bapool.retrofit.data

import java.time.LocalDateTime

data class GetResGroupListResponse(
    val code: Int,
    val message: String,
    val result: GetRestaurantPartiesListResult,
)

data class MyPartyListModel(
    val grpId: Long,
    val resName: String = "",
    val grpName: String = "",
    val participants: Int,
    val max_people: Int,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val lastChat: String = "",
    val notReadChat: Int,
)

data class ResPartyList(
    val detail: String,
    val end_date: String,
    val has_block_user: Boolean,
    val hashtag: List<Int>,
    val max_people: Int,
    val menu: String,
    val participants: Int,
    val party_id: Int,
    val party_name: String,
    val rating: List<Double>,
    val start_date: String,
    val is_participate: Boolean,
)

data class PostMakePartyRequest(

    val detail: String,
    val end_date: String,
    val hashtag: List<Int>,
    val imgUrl: String,
    val max_people: Int,
    val menu: String,
    val party_name: String,
    val restaurant_info: PostMakePartyRequestRestaurantInfo,
    val start_date: String,
)

data class PostMakePartyRequestRestaurantInfo(
    val address: String,
    val category: String,
    val img_url: String,
    val name: String,
    val phone: String,
    val restaurant_id: Int,
    val site_url: String,
)


data class Result(
    val party_id: Long,
)

data class PostMakePartyResponse(
    val code: Int,
    val message: String,
    val result: Result,
)

data class FirebasePartyInfo(
    val groupName: String = "",
    val groupMenu: String = "",
    val groupDetail: String = "",
    val curNumberOfPeople: Int = 0,
    val maxNumberOfPeople: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val hashTag: List<Int> = listOf(),
)

data class FirebasePartyMessage(
    var senderId: String = "",
    var sendedDate: String = "",
    var content: String = "",
    var type: Int = 0,
    var downloadUrl: String = "",
    var confirmed: MutableMap<String, Boolean> = HashMap(),

    )


data class FirebaseParty(
    val groupInfo: FirebasePartyInfo,
    val groupUsers: Map<String, Boolean> = HashMap(),
)


data class GetRestaurantPartiesListResult(
    val parties: List<ResPartyList>,
    val restaurant_name: String,
)