package com.yes.tfgapp.data.network

import com.yes.tfgapp.data.network.response.ProductSearchResponse
import com.yes.tfgapp.data.network.response.StockProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductsApiService {

    @GET("cgi/search.pl")
    suspend fun searchApiProducts(
        @Query("search_terms") searchTerms: String,
        @Query("action") action: String = "process",
        @Query("json") json: Boolean = true,
        @Query("page_size") pageSize: Int = 24,
        @Query("page") page: Int = 1
    ): Response<ProductSearchResponse>

    @GET("api/v2/product/{id}")
    suspend fun getProduct(@Path("id") id: String): Response<StockProductResponse>

}