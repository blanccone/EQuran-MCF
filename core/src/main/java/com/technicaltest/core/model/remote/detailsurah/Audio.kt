package com.technicaltest.core.model.remote.detailsurah

import com.google.gson.annotations.SerializedName

data class Audio(
    @SerializedName("01")
    val audio01: String,
    @SerializedName("02")
    val audio02: String,
    @SerializedName("03")
    val audio03: String,
    @SerializedName("04")
    val audio04: String,
    @SerializedName("05")
    val audio05: String
)