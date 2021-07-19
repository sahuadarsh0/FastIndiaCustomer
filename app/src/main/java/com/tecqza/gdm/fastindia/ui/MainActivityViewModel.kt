package com.tecqza.gdm.fastindia.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.tecqza.gdm.fastindia.data.remote.CustomerService
import com.tecqza.gdm.fastindia.model.Home
import com.tecqza.gdm.fastindia.model.HomeItem

class MainActivityViewModel() : ViewModel() {
//    (application: Application) : AndroidViewModel(application) {

//    private val db = Room.databaseBuilder(
//        application.applicationContext,
//        CartDatabase::class.java, "Cart-Database"
//    ).build()

//    private val productsDao = db.productsDao()
//
//    private val _productsLiveData = MutableLiveData<List<Product>>()
//
//    val productLiveData: LiveData<List<Product>>
//        get() = _productsLiveData
//
//    fun getProducts() {
//        viewModelScope.launch {
//            productsDao.getAll()
//        }
//    }

    fun getHome() = liveData {
        val home = CustomerService.create().home()
        emit(home)
    }

    var jsonHome: MutableLiveData<Home?> =
        MutableLiveData<Home?>()

    fun setHomeVariable(home: Home?) {
        this.jsonHome.value = home
    }

    var jsonHomeItem: MutableLiveData<HomeItem?> =
        MutableLiveData<HomeItem?>()

    fun setHomeItemVariable(home: HomeItem?) {
        this.jsonHomeItem.value = home
    }
}
