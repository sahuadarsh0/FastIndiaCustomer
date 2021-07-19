package com.tecqza.gdm.fastindia.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tecqza.gdm.fastindia.R
import com.tecqza.gdm.fastindia.data.remote.CustomerService.Companion.OFFER_URL
import com.tecqza.gdm.fastindia.databinding.ListOfferBinding
import com.tecqza.gdm.fastindia.model.Slider

class OfferAdapter : RecyclerView.Adapter<OfferAdapter.OfferViewHolder>() {
    private val offerList = ArrayList<Slider>()
    lateinit var listener: ItemClickListener
    var rowIndex = 0

    fun setList(slider: ArrayList<Slider>) {
        offerList.clear()
        offerList.addAll(slider)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {

        val binding = ListOfferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OfferViewHolder(binding)

    }

    override fun getItemCount(): Int = offerList.size


    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {

        with(holder) {
            bind(offerList[position])
            rowHomeHolder.setOnClickListener {
                rowIndex = position
                notifyDataSetChanged()
                listener.onItemClickListener(offerList[position])
            }
        }
    }

    inner class OfferViewHolder(val binding: ListOfferBinding) : RecyclerView.ViewHolder(binding.root) {

        lateinit var rowHomeHolder: ConstraintLayout

        fun bind(slider: Slider) {

            binding.apply {
                val imgUrl = OFFER_URL + slider.image
                Glide.with(offerImage.context)
                    .load(imgUrl)
                    .placeholder(R.drawable.f_icon)
                    .centerCrop()
                    .into(offerImage)
//            binding.orderNow.setOnClickListener {
//                listener.onItemClickListener(vendor, binding.vendorImage)
//            }
                rowHomeHolder = rowSlider

            }

        }

    }

    interface ItemClickListener {
        fun onItemClickListener(slider: Slider)
    }
}