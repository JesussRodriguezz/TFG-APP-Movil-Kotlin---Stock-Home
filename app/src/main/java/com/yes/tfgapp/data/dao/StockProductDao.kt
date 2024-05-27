package com.yes.tfgapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yes.tfgapp.domain.model.StockProductModel

@Dao
interface StockProductDao {
    @Insert
    suspend fun addStockProduct(stockProduct: StockProductModel)

    @Insert
    suspend fun addStockProducts(stockProducts: List<StockProductModel>)

    @Update
    suspend fun updateStockProduct(stockProduct: StockProductModel)

    @Delete
    suspend fun deleteStockProduct(stockProduct: StockProductModel)

    @Query("SELECT * FROM stock_product WHERE id = :id")
    suspend fun getStockProductById(id: Int): StockProductModel

    @Query("SELECT * FROM stock_product ORDER BY id ASC")
    fun readAllData(): LiveData<List<StockProductModel>>

    @Query("SELECT * FROM stock_product WHERE name = :name LIMIT 1")
    fun findStockProductByName(name: String): StockProductModel

    @Query("SELECT * FROM stock_product")
    suspend fun getAllStockProducts(): List<StockProductModel>

    @Query("SELECT * FROM stock_product ORDER BY name ASC")
    fun getStockProductsOrderedByNameAsc(): LiveData<List<StockProductModel>>

    @Query("SELECT * FROM stock_product ORDER BY name DESC")
    fun getStockProductsOrderedByNameDesc(): LiveData<List<StockProductModel>>

    @Query("SELECT * FROM stock_product ORDER BY addedDate ASC")
    fun getStockProductsOrderedByAddedDateAsc(): LiveData<List<StockProductModel>>

    @Query("SELECT * FROM stock_product ORDER BY addedDate DESC")
    fun getStockProductsOrderedByAddedDateDesc(): LiveData<List<StockProductModel>>

    @Query("SELECT * FROM stock_product ORDER BY expirationDate ASC")
    fun getStockProductsOrderedByExpiryDateAsc(): LiveData<List<StockProductModel>>

    @Query("SELECT * FROM stock_product ORDER BY expirationDate DESC")
    fun getStockProductsOrderedByExpiryDateDesc(): LiveData<List<StockProductModel>>
}