package com.yes.tfgapp.data.network

import com.yes.tfgapp.data.network.response.ProductSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductsApiService {

    @GET("api/v2/search")
    suspend fun searchProducts(@Query("search_terms") searchTerms: String): Response <ProductSearchResponse>

    @GET("api/v2/product/{id}")
    suspend fun getProduct(@Path("id") id: String): Response<ProductSearchResponse>

}