package com.tecqza.gdm.fastindia.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.tecqza.gdm.fastindia.databinding.ListHomeBinding
import com.tecqza.gdm.fastindia.model.Home
import com.tecqza.gdm.fastindia.model.HomeItem

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    private val categoryList = Home()
    lateinit var listener: ItemClickListener
    var rowIndex = 0

    fun setList(home: Home) {
        categoryList.clear()
        categoryList.addAll(home)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {

        val binding = ListHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }


    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        with(holder) {
            bind(categoryList[position])
            rowHomeHolder.setOnClickListener {
                rowIndex = position
                notifyDataSetChanged()
                listener.onItemClickListener(categoryList[position] )
            }
            if (rowIndex == position) {
                cardNameHolder.visibility = View.VISIBLE
                nameHolder.visibility = View.GONE
            } else {
                nameHolder.visibility = View.VISIBLE
                cardNameHolder.visibility = View.GONE
            }
        }
    }

    inner class CategoryViewHolder(val binding: ListHomeBinding) : RecyclerView.ViewHolder(binding.root) {

        lateinit var rowHomeHolder: ConstraintLayout
        lateinit var cardNameHolder: CardView
        lateinit var nameHolder: TextView


        fun bind(homeItem: HomeItem) {

            binding.apply {
                name.text = homeItem.name
                name2.text = homeItem.name
//        binding.address.text = vendor.address
//            val imgUrl = VENDOR_URL + homeItem.logo
//            Glide.with(binding.vendorImage.context)
//                .load(imgUrl)
//                .placeholder(R.drawable.f_icon)
//                .centerCrop()
//                .into(binding.vendorImage)
//            binding.orderNow.setOnClickListener {
//                listener.onItemClickListener(vendor, binding.vendorImage)
//            }
                rowHomeHolder = rowHome
                cardNameHolder = cardName
                nameHolder = name

            }

        }

    }

    interface ItemClickListener {
        fun onItemClickListener(homeItem: HomeItem)
    }
}