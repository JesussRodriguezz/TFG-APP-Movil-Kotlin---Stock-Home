package com.yes.tfgapp.data.repository

import androidx.lifecycle.LiveData
import com.yes.tfgapp.data.dao.ProductShoppingListDao
import com.yes.tfgapp.domain.model.ProductShoppingListModel

class ProductShoppingListRepository(private val productShoppingListDAO: ProductShoppingListDao) {

    //val readAllData: List<ProductShoppingListModel> = productShoppingListDAO.readAllData()

    suspend fun addProductShoppingList(productShoppingList: ProductShoppingListModel) {
        val existingProductShoppingList = productShoppingListDAO.getProductShoppingList(
            productShoppingList.productId,
            productShoppingList.shoppingListId
        )
        println("Existing product shopping list: $existingProductShoppingList")
        if (existingProductShoppingList == null) {
            println("Adding product shopping list")
            productShoppingListDAO.addProductShoppingList(productShoppingList)
            productShoppingListDAO.incrementQuantity(productShoppingList.shoppingListId)
        } else {
            println("Product shopping list already exists")
        }
    }

    suspend fun updateProductShoppingListBoughtProduct(productShoppingList: ProductShoppingListModel) {
        productShoppingListDAO.updateProductShoppingList(productShoppingList)
        productShoppingListDAO.incrementQuantityBought(productShoppingList.shoppingListId)
    }

    suspend fun updateProductShoppingListNotBoughtProduct(productShoppingList: ProductShoppingListModel) {
        productShoppingListDAO.updateProductShoppingList(productShoppingList)
        productShoppingListDAO.decrementQuantityBought(productShoppingList.shoppingListId)
    }


    fun getProductsForShoppingList(id: Int): LiveData<List<ProductShoppingListModel>> {
        return productShoppingListDAO.getAllMyProductsToBuy(id)

    }

    fun getProductsBoughtForShoppingList(id: Int): LiveData<List<ProductShoppingListModel>> {
        return productShoppingListDAO.getAllMyProductsBought(id)

    }


}