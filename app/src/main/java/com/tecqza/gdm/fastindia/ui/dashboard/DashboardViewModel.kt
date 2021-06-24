package com.tecqza.gdm.fastindia.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.tecqza.gdm.fastindia.data.remote.CustomerService

class DashboardViewModel : ViewModel() {

    fun getVendors() = liveData {
        val vendorList = CustomerService.create().getVendorList()
        emit(vendorList)
    }
}