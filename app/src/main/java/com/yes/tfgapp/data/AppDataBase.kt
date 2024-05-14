package com.yes.tfgapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yes.tfgapp.data.dao.CategoryDao
import com.yes.tfgapp.data.dao.ProductDao
import com.yes.tfgapp.data.dao.ProductShoppingListDao
import com.yes.tfgapp.data.dao.ShoppingListDao
import com.yes.tfgapp.data.dao.StockProductDao
import com.yes.tfgapp.domain.model.CategoryModel
import com.yes.tfgapp.domain.model.ProductModel
import com.yes.tfgapp.domain.model.ProductShoppingListModel
import com.yes.tfgapp.domain.model.ShoppingListModel
import com.yes.tfgapp.domain.model.StockProductModel

@Database(entities = [ShoppingListModel::class, ProductModel::class,CategoryModel::class, ProductShoppingListModel::class,StockProductModel::class ], version = 1, exportSchema = false)
abstract class AppDataBase: RoomDatabase(){
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productShoppingListDao(): ProductShoppingListDao
    abstract fun stockProductDao(): StockProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}