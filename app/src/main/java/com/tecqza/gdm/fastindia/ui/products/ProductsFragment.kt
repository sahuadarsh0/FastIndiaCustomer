package com.tecqza.gdm.fastindia.ui.products

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.bumptech.glide.Glide
import com.tecqza.gdm.fastindia.R
import com.tecqza.gdm.fastindia.data.local.database.CartDatabase
import com.tecqza.gdm.fastindia.data.remote.CustomerService
import com.tecqza.gdm.fastindia.databinding.FragmentProductsBinding
import com.tecqza.gdm.fastindia.ui.adapters.ProductAdapter

class ProductsFragment : Fragment() {

    private lateinit var productsViewModel: ProductsViewModel
    private var _binding: FragmentProductsBinding? = null

    private val binding get() = _binding!!
    private val adapter = ProductAdapter()
    val args: ProductsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        productsViewModel =
            ViewModelProvider(this).get(ProductsViewModel::class.java)

        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedElementEnterTransition = TransitionInflater.from(this.context).inflateTransition(R.transition.change_bounds)
        sharedElementReturnTransition = TransitionInflater.from(this.context).inflateTransition(R.transition.change_bounds)

        val imgUrl = CustomerService.VENDOR_URL + args.vendorImage
        Glide.with(requireContext())
            .load(imgUrl)
            .placeholder(R.drawable.f_icon)
            .centerCrop()
            .into(binding.vendorImage)
        binding.vendorMobile.text = args.vendorMobile
        binding.vendorName.text = args.vendorName
        initRecyclerView()
        return root
    }

    private fun initRecyclerView() {
        binding.productList.layoutManager = LinearLayoutManager(context)
        binding.productList.adapter = adapter
        getData()
    }

    private fun getData() {
        binding.progressBar.visibility = View.VISIBLE
        val responseLiveData = productsViewModel.getProducts(args.vendorId)
        responseLiveData.observe(viewLifecycleOwner, {
            if (it != null) {
                it.body()?.let { it1 -> adapter.setList(it1) }
                adapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, "No data available", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}