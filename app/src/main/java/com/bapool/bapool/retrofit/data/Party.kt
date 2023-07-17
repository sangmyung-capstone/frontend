package com.bapool.bapool.retrofit.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetResPartyListResponse(
    val code: Int,
    val message: String,
    val result: GetRestaurantPartiesListResult,
)

data class MyPartyListModel(
    val grpId: String?,
    val resName: String = "",
    val grpName: String = "",
    val participants: Any?,
    val lastChat: String = "",
    val notReadChat: Int,
    val lastChatTime: String = "",
)

data class ResPartyList(
    val detail: String,
    val end_date: String,
    val has_block_user: Boolean,
    @SerializedName("party_hashtag")
    val hashtag: List<Int>,
    val max_people: Int,
    val menu: String,
    val participants: Int,
    val party_id: Int,
    val party_name: String,
    @SerializedName("user_rating")
    val rating: List<Double>,
    val start_date: String,
    val is_participate: Boolean,
    val is_recruiting : Boolean
)


data class PostMakePartyRequest(
    val party_name: String,
    val max_people: Int,
    val start_date: String,
    val end_date: String,
    val menu: String,
    val hashtag: List<Int>,
    val detail: String,
    val restaurant_info: PostMakePartyRequestRestaurantInfo,
)

data class PatchEditPartyInfoRequest(
    val party_id: Long,
    val party_name: String,
    val max_people: Int,
    val start_date: String,
    val end_date: String,
    val menu: String,
    val detail: String,
)

data class PatchEditPartyInfoResponse(
    val code: Int,
    val message: String,
)


data class PostMakePartyRequestRestaurantInfo(
    val restaurant_id: Int,
    val name: String,
    val address: String,
    val img_url: String,
    val site_url: String,
    val category: String,
    val phone: String,
)


data class Result(
    val party_id: Long,
)

data class PostMakePartyResponse(
    val code: Int,
    val message: String,
    val result: Result,
)

data class FirebasePartyInfo
    (
    val groupName: String = "",
    val groupMenu: String = "",
    val groupDetail: String = "",
    val curNumberOfPeople: Int = 0,
    val maxNumberOfPeople: Int = 0,
    val startDate: String = "",
    val endDate: String = "",
    val hashTag: List<Int> = listOf(),
    val restaurantName: String = "",
    val groupLeaderId: Int = 0,
    val siteUrls: String = "",
): Serializable

data class FirebasePartyMessage(
    var senderId: String = "",
    var sendedDate: String = "",
    var content: String = "",
    var type: Int = 0,
    var downloadUrl: String = "",
    var confirmed: MutableMap<String, Boolean> = HashMap(),
)

data class FirebaseParty(
    val groupInfo: FirebasePartyInfo = FirebasePartyInfo(),
    val groupMessages: Map<String, FirebasePartyMessage>? = HashMap(),
    val groupUsers: Map<String, Boolean>? = HashMap(),
)

data class GetRestaurantPartiesListResult(
    val parties: List<ResPartyList>?,
    val restaurant_name: String,
)




