package com.tecqza.gdm.fastindia.ui.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tecqza.gdm.fastindia.R
import com.tecqza.gdm.fastindia.data.remote.CustomerService.Companion.PRODUCT_URL
import com.tecqza.gdm.fastindia.databinding.ListProductBinding
import com.tecqza.gdm.fastindia.model.Product


class ProductAdapter : RecyclerView.Adapter<ProductsViewHolder>() {
    private val productList = ArrayList<Product>()

    fun setList(product: ArrayList<Product>) {
        productList.clear()
        productList.addAll(product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListProductBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.list_product,
            parent,
            false
        )
        return ProductsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        holder.bind(productList[position])
    }
}


class ProductsViewHolder(private val binding: ListProductBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(product: Product) {
        binding.productName.text = product.name
        binding.productDescription.text = product.description
        binding.mrp.text = "\u20A8 ${product.mrp}"
        binding.sellingPrice.text = "  \u20A8 ${product.selling}"
        val imgUrl = PRODUCT_URL + product.image
        Glide.with(binding.productImage.context)
            .load(imgUrl)
            .placeholder(R.drawable.f_icon)
            .centerCrop()
            .circleCrop()
            .into(binding.productImage)
        binding.productCard.setOnClickListener {

        }

        binding.mrp.paintFlags = binding.mrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

    }
}