package com.tecqza.gdm.fastindia.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.tecqza.gdm.fastindia.model.Product

@Dao
interface ProductsDao {
    @Query("SELECT * FROM products")
   suspend fun getAll(): List<Product>

    @Insert
    suspend fun insert(vararg products: Product)

    @Delete
    suspend fun delete(product: Product)
}