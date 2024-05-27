package com.yes.tfgapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ProductShoppingListModel

@Dao
interface ProductShoppingListDao {

    @Insert
    suspend fun addProductShoppingList(productShoppingList: ProductShoppingListModel)

    @Update
    suspend fun updateProductShoppingList(productShoppingList: ProductShoppingListModel)

    @Delete
    suspend fun deleteProductShoppingList(productShoppingList: ProductShoppingListModel)

    @Query("SELECT * FROM product_shopping_list WHERE productId = :productId AND shoppingListId = :shoppingListId")
    fun getProductShoppingList(productId: Int, shoppingListId: Int): ProductShoppingListModel

    @Query("SELECT * FROM product_shopping_list WHERE shoppingListId = :id")
    fun getAllMyProducts(id: Int): LiveData<List<ProductShoppingListModel>>

    @Query("SELECT * FROM product_shopping_list WHERE shoppingListId = :id AND isBought = 0")
    fun getAllMyProductsToBuy(id: Int): LiveData<List<ProductShoppingListModel>>

    @Query("SELECT * FROM product_shopping_list WHERE shoppingListId = :id AND isBought = 1")
    fun getAllMyProductsBought(id: Int): LiveData<List<ProductShoppingListModel>>

    @Query("UPDATE shopping_list SET quantity = quantity + 1 WHERE id = :shoppingListId")
    fun incrementQuantity(shoppingListId: Int)

    @Query("UPDATE shopping_list SET quantity = quantity - 1 WHERE id = :shoppingListId")
    fun decrementQuantity(shoppingListId: Int)

    @Query("UPDATE shopping_list SET quantityBought = quantityBought + 1 WHERE id = :shoppingListId")
    fun incrementQuantityBought(shoppingListId: Int)

    @Query("UPDATE shopping_list SET quantityBought = quantityBought - 1 WHERE id = :shoppingListId")
    fun decrementQuantityBought(shoppingListId: Int)

    @Query("SELECT * FROM product_shopping_list WHERE shoppingListId = :id")
    fun getAllProductsInShoppingList(id: Int): LiveData<List<ProductShoppingListModel>>

    @Query("DELETE FROM product_shopping_list WHERE shoppingListId = :id")
    suspend fun emptyAllList(id: Int)

    @Query("UPDATE shopping_list SET quantity = 0, quantityBought = 0 WHERE id = :id")
    fun decrementAllQuantities(id: Int)


}