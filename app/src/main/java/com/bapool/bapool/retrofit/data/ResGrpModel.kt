package com.bapool.bapool.retrofit.data

import com.google.gson.annotations.SerializedName

data class ResGrpModel(
    val restaurant_name: String,
    val groups: List<ResGrpListModel>

)