package com.tecqza.gdm.fastindia.ui.dashboard

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
import com.tecqza.gdm.fastindia.databinding.FragmentDashboardBinding
import com.tecqza.gdm.fastindia.model.Vendors
import com.tecqza.gdm.fastindia.ui.WebPage
import com.tecqza.gdm.fastindia.ui.adapters.VendorAdapter

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!
    private val adapter = VendorAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initRecyclerView()
        binding.banner.setOnClickListener {
            startWeb("https://play.google.com/store/apps/details?id=nic.goi.aarogyasetu&hl=en_IN&gl=US")
        }

        return root
    }

    private fun initRecyclerView() {
        binding.vendorList.layoutManager = LinearLayoutManager(context)
        binding.vendorList.adapter = adapter
        adapter.listener = object : VendorAdapter.ItemClickListener {
            override fun onItemClickListener(vendor: Vendors, imageView: ImageView) {

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
        val responseLiveData = dashboardViewModel.getVendors()
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