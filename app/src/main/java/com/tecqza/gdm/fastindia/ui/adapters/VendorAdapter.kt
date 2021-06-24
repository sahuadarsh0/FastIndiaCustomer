package com.tecqza.gdm.fastindia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tecqza.gdm.fastindia.R
import com.tecqza.gdm.fastindia.data.remote.CustomerService.Companion.VENDOR_URL
import com.tecqza.gdm.fastindia.databinding.ListVendorBinding
import com.tecqza.gdm.fastindia.model.Vendors

class VendorAdapter : RecyclerView.Adapter<VendorAdapter.MyViewHolder>() {
    private val vendorList = ArrayList<Vendors>()
    lateinit var listener: ItemClickListener

    fun setList(vendor: ArrayList<Vendors>) {
        vendorList.clear()
        vendorList.addAll(vendor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListVendorBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.list_vendor,
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return vendorList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(vendorList[position])
    }

    inner class MyViewHolder(private val binding: ListVendorBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(vendor: Vendors) {
//        binding.name.text = vendor.name
//        binding.address.text = vendor.address
            val imgUrl = VENDOR_URL + vendor.logo
            Glide.with(binding.vendorImage.context)
                .load(imgUrl)
                .placeholder(R.drawable.f_icon)
                .centerCrop()
                .into(binding.vendorImage)
            binding.orderNow.setOnClickListener {
                listener.onItemClickListener(vendor, binding.vendorImage)
            }
        }

    }

    interface ItemClickListener {
        fun onItemClickListener(vendor: Vendors, imageView: ImageView)
    }
}