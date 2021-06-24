package com.tecqza.gdm.fastindia.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.tecqza.gdm.fastindia.model.Products

@Dao
interface ProductsDao {
    @Query("SELECT * FROM products")
   suspend fun getAll(): List<Products>

    @Insert
    suspend fun insert(vararg products: Products)

    @Delete
    suspend fun delete(products: Products)
}