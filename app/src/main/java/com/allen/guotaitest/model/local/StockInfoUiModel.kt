package com.allen.guotaitest.model.local

data class StockInfoUiModel(
    val code: String,
    val name: String? = "",
    val date: String? = "",
    val dividendYield: String? = "",
    val pBratio: String? = "",
    val pEratio: String? = "",
    val closingPrice: String? = "",
    val monthlyAveragePrice: String? = "",
    val change: String? = "",
    val highestPrice: String? = "",
    val lowestPrice: String? = "",
    val openingPrice: String? = "",
    val tradeValue: String? = "",
    val tradeVolume: String? = "",
    val transaction: String? = ""
) {
}