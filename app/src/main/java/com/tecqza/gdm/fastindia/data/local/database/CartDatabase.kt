package com.tecqza.gdm.fastindia.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tecqza.gdm.fastindia.data.local.dao.ProductsDao
import com.tecqza.gdm.fastindia.model.Product

@Database(entities = [Product::class],exportSchema = false,version = 1)
abstract class CartDatabase : RoomDatabase() {
    abstract fun productsDao(): ProductsDao
}