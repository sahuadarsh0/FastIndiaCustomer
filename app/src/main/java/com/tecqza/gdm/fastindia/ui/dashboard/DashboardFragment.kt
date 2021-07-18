package com.tecqza.gdm.fastindia.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tecqza.gdm.fastindia.databinding.FragmentDashboardBinding
import com.tecqza.gdm.fastindia.model.HomeItem
import com.tecqza.gdm.fastindia.ui.MainActivityViewModel
import com.tecqza.gdm.fastindia.ui.WebPage
import com.tecqza.gdm.fastindia.ui.adapters.CategoryAdapter

class DashboardFragment : Fragment() {

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!
    private val adapter = CategoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainActivityViewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initRecyclerView()
//        binding.banner.setOnClickListener {
//            startWeb("https://play.google.com/store/apps/details?id=nic.goi.aarogyasetu&hl=en_IN&gl=US")
//        }

        return root
    }

    private fun initRecyclerView() {
        binding.homeList.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        binding.homeList.adapter = adapter
        adapter.listener = object : CategoryAdapter.ItemClickListener {
            override fun onItemClickListener(homeItem: HomeItem, imageView: ImageView) {
                if (!homeItem.name.isNullOrBlank()) {
                    Toast.makeText(context, "clicked ${homeItem.name}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        getData()
    }

    private fun getData() {
        binding.progressBar.visibility = View.VISIBLE
        mainActivityViewModel.jsonHome.observe(viewLifecycleOwner, {
            if (it != null) {
                it.let { home -> adapter.setList(home) }
                adapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(context, "No data available", Toast.LENGTH_LONG).show()
            }
        })
    }

//    private fun initRecyclerView() {
//        binding.homeList.layoutManager = LinearLayoutManager(context)
//        binding.homeList.adapter = adapter
//        adapter.listener = object : VendorAdapter.ItemClickListener {
//            override fun onItemClickListener(vendor: Vendor, imageView: ImageView) {
//
//                if (vendor.url.isNullOrBlank()) {
//                    val extras = FragmentNavigatorExtras(
//                        imageView to "vendor"
//                    )
//                    val action = DashboardFragmentDirections
//                        .actionNavigationDashboardToProductsFragment(
//                            vendor.vendorId!!,
//                            vendor.logo!!,
//                            vendor.mobile!!,
//                            vendor.name!!
//                        )
//                    findNavController().navigate(action, extras)
//                }
//                else startWeb(vendor.url)
//            }
//        }
//        getData()
//    }

//    private fun getData() {
//        binding.progressBar.visibility = View.VISIBLE
//        val responseLiveData = dashboardViewModel.getVendors()
//        responseLiveData.observe(viewLifecycleOwner, {
//            if (it != null) {
//                it.body()?.let { it1 -> adapter.setList(it1) }
//                adapter.notifyDataSetChanged()
//                binding.progressBar.visibility = View.GONE
//            } else {
//                binding.progressBar.visibility = View.GONE
//                Toast.makeText(context, "No data available", Toast.LENGTH_LONG).show()
//            }
//        })
//    }


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