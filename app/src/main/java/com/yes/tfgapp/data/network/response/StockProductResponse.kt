package com.yes.tfgapp.data.network.response
import com.google.gson.annotations.SerializedName
data class StockProductResponse(
    @SerializedName("code") val code: String,
    @SerializedName("product") val product: StockProductDetailResponse
)

data class StockProductDetailResponse(
    @SerializedName("product_name") val productName: String,
    @SerializedName("image_url") val productImage:String,
    @SerializedName("nutriscore_grade") val nutriscoreGrade: String,
    @SerializedName("nutriscore_score") val nutriscoreScore: Int,
    @SerializedName("ingredients_text") val ingredientsText: String,
    @SerializedName("ingredients_text_es") val ingredientsTextEs: String,
    @SerializedName("quantity") val quantity: String,
    @SerializedName("serving_size") val servingSize: String,
    @SerializedName("nutrient_levels") val nutrimentsLevels: NutrientsLevels,
    @SerializedName("nutriments") val nutriments: Nutrients,
    @SerializedName("generic_name_es") val genericNameEs: String,
    @SerializedName("brands") val brands: String,
)

data class NutrientsLevels(
    @SerializedName("fat") val fat: String,
    @SerializedName("salt") val salt: String,
    @SerializedName("saturated-fat") val saturatedFat: String,
    @SerializedName("sugars") val sugars: String
)

data class Nutrients(
    @SerializedName("energy-kcal_100g") val energyKcal100g: Float,
    @SerializedName("energy-kcal_serving") val energyKcalServing: Float,
    @SerializedName("energy-kcal_unit") val energyKcalUnit: String,
    @SerializedName("fat_100g") val fat100g: Float,
    @SerializedName("fat_serving") val fatServing: Float,
    @SerializedName("fat_unit") val fatUnit: String,
    @SerializedName("saturated-fat_100g") val saturatedFat100g: Float,
    @SerializedName("saturated-fat_serving") val saturatedFatServing: Float,
    @SerializedName("saturated-fat_unit") val saturatedFatUnit: String,
    @SerializedName("carbohydrates_100g") val carbohydrates100g: Float,
    @SerializedName("carbohydrates_serving") val carbohydratesServing: Float,
    @SerializedName("carbohydrates_unit") val carbohydratesUnit: String,
    @SerializedName("salt_100g") val salt100g: Float,
    @SerializedName("salt_serving") val saltServing: Float,
    @SerializedName("salt_unit") val saltUnit: String,
    @SerializedName("proteins_100g") val proteins100g: Float,
    @SerializedName("proteins_serving") val proteinsServing: Float,
    @SerializedName("proteins_unit") val proteinsUnit: String

)
