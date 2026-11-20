package com.allen.guotaitest.model.remote

import androidx.annotation.Keep
import com.squareup.moshi.Json
import kotlinx.serialization.Serializable

class BWIBBUModel {

    @Keep
    @Serializable
    data class Response(
        @Json(name = "Code")
        val code: String,
        @Json(name = "Date")
        val date: String?,
        @Json(name = "DividendYield")
        val dividendYield: String?,
        @Json(name = "Name")
        val name: String?,
        @Json(name = "PBratio")
        val pBratio: String?,
        @Json(name = "PEratio")
        val pEratio: String?
    )
}