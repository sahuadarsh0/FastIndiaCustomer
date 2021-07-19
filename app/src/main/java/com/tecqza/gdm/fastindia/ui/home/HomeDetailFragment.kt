package com.tecqza.gdm.fastindia.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tecqza.gdm.fastindia.databinding.FragmentHomeDetailsBinding
import com.tecqza.gdm.fastindia.model.Slider
import com.tecqza.gdm.fastindia.model.Vendor
import com.tecqza.gdm.fastindia.ui.MainActivityViewModel
import com.tecqza.gdm.fastindia.ui.WebPage
import com.tecqza.gdm.fastindia.ui.adapters.OfferAdapter
import com.tecqza.gdm.fastindia.ui.adapters.VendorAdapter
import com.tecqza.gdm.fastindia.ui.dashboard.DashboardFragmentDirections

class HomeDetailFragment : Fragment() {

    private lateinit var ordersViewModel: HomeDetailViewModel
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var _binding: FragmentHomeDetailsBinding? = null
    private val offerAdapter = OfferAdapter()
    private val vendorAdapter = VendorAdapter()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivityViewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)

        _binding = FragmentHomeDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initRecyclerView()
//        binding.banner.setOnClickListener {
//            startWeb("https://play.google.com/store/apps/details?id=nic.goi.aarogyasetu&hl=en_IN&gl=US")
//        }
        binding.progressBar.visibility = View.VISIBLE
        return root

    }


    private fun initRecyclerView() {
        binding.offerList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.offerList.adapter = offerAdapter
        offerAdapter.listener = object :
            OfferAdapter.ItemClickListener {
            override fun onItemClickListener(slider: Slider) {

                Toast.makeText(context, "${slider.categoryId}", Toast.LENGTH_SHORT).show()
//                val bundle = bundleOf("amount" to slider)
//                findNavController().navigate(R.id.homeDetailFragment, bundle)
            }
        }



        binding.vendorList.layoutManager = LinearLayoutManager(context)
        binding.vendorList.adapter = vendorAdapter
        vendorAdapter.listener = object : VendorAdapter.ItemClickListener {
            override fun onItemClickListener(vendor: Vendor, imageView: ImageView) {

                if (vendor.url.isNullOrBlank()) {
                    val extras = FragmentNavigatorExtras(
                        imageView to "vendor"
                    )
                    val action = DashboardFragmentDirections
                        .actionNavigationDashboardToProductsFragment(
                            vendor.vendorId!!,
                            vendor.logo!!,
                            vendor.mobile!!,
                            vendor.name!!
                        )
                    findNavController().navigate(action, extras)
                }
                else startWeb(vendor.url)
            }
        }
        getData()
    }

    private fun getData() {
        binding.progressBar.visibility = View.VISIBLE

        mainActivityViewModel.jsonHomeItem.observe(viewLifecycleOwner, {
            if (it != null) {
                it.let { homeItem ->
                    offerAdapter.setList(homeItem.slider!!)
                    vendorAdapter.setList(homeItem.vendor!!)
                }
                offerAdapter.notifyDataSetChanged()
                vendorAdapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, "No data available", Toast.LENGTH_LONG).show()
            }
        })
    }


        private fun startWeb(url: String) {
            val i = Intent(context, WebPage::class.java)
            i.putExtra("url", url)
            startActivity(i)
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }