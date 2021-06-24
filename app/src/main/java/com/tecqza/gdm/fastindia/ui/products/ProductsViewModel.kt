package com.tecqza.gdm.fastindia.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.tecqza.gdm.fastindia.data.remote.CustomerService

class ProductsViewModel : ViewModel() {

    fun getProducts(vendorId : String) = liveData {
        val vendorList = CustomerService.create().getProductList(vendorId)
        emit(vendorList)
    }
}