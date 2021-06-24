package com.tecqza.gdm.fastindia.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.tecqza.gdm.fastindia.data.local.database.CartDatabase
import com.tecqza.gdm.fastindia.model.Products
import kotlinx.coroutines.launch

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application.applicationContext,
        CartDatabase::class.java, "Cart-Database"
    ).build()

    private val productsDao = db.productsDao()

    private val _productsLiveData = MutableLiveData<List<Products>>()

    val productsLiveData: LiveData<List<Products>>
        get() = _productsLiveData

    fun getProducts() {
        viewModelScope.launch {
            productsDao.getAll()
        }
    }
}
