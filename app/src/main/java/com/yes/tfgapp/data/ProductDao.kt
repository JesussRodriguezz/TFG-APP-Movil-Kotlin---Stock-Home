package com.yes.tfgapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yes.tfgapp.domain.model.ProductModel

@Dao
interface ProductDao {

    @Insert
    suspend fun addProduct(product: ProductModel)

    @Update
    suspend fun updateProduct(product: ProductModel)

    @Delete
    suspend fun deleteProduct(product: ProductModel)

    @Query("SELECT * FROM product WHERE id = :id")
    suspend fun getProductById(id: Int):ProductModel?

    @Query("SELECT * FROM product ORDER BY id ASC")
    fun readAllData(): LiveData<List<ProductModel>>

    @Query("SELECT * FROM product WHERE id = :productId")
    abstract fun getProduct(productId: Int): ProductModel


    @Query("SELECT * FROM product WHERE id IN (:productIds)")
    fun getProductsById(productIds: List<Int>): LiveData<List<ProductModel>>

    //@Query("SELECT * FROM product WHERE category = :name")
    //fun productsByCategory(name: String): LiveData<List<ProductModel>>

    //@Query("SELECT * FROM product WHERE category = :name")
    //fun productsByCategoryNoLive(name: String): List<ProductModel>


}