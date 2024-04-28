package com.yes.tfgapp.data.network.response

import com.google.gson.annotations.SerializedName

data class ProductSearchResponse(
    @SerializedName("products") val products: List<ProductItemResponse>
)

data class ProductItemResponse(
    @SerializedName("_id") val productId: String,
    @SerializedName("generic_name") val productName: String,
    @SerializedName("image_front_small_url") val productImage:String

)
