package com.yes.tfgapp.data.network.response
import com.google.gson.annotations.SerializedName
data class StockProductResponse(
    @SerializedName("code") val code: String,
    @SerializedName("product") val product: StockProductDetailResponse
)

data class StockProductDetailResponse(
    @SerializedName("product_name") val productName: String,
    @SerializedName("image_url") val productImage:String
)