package com.tecqza.gdm.fastindia.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tecqza.gdm.fastindia.R
import com.tecqza.gdm.fastindia.databinding.FragmentHomeDetailsBinding
import com.tecqza.gdm.fastindia.model.Slider
import com.tecqza.gdm.fastindia.ui.WebPage
import com.tecqza.gdm.fastindia.ui.adapters.OfferAdapter

class HomeDetailFragment : Fragment() {

    private lateinit var ordersViewModel: HomeDetailViewModel
    private var _binding: FragmentHomeDetailsBinding? = null
    private val adapter = OfferAdapter()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ordersViewModel =
            ViewModelProvider(this).get(HomeDetailViewModel::class.java)

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
        binding.offerList.adapter = adapter
        adapter.listener = object : OfferAdapter.ItemClickListener {
            override fun onItemClickListener(slider: Slider) {

                Toast.makeText(context, "${slider.categoryId}", Toast.LENGTH_SHORT).show()
//                val bundle = bundleOf("amount" to slider)
//                findNavController().navigate(R.id.homeDetailFragment, bundle)
            }
        }
        getData()
    }

    private fun getData() {
//        binding.progressBar.visibility = View.VISIBLE

     val homeItem =   arguments?.getBundle("homeItem")

//        HomeDetailFragment().arguments
        Log.d("asa", "getData: $homeItem")
//        if (homeItem != null) {
//            homeItem.getParcelableArrayList<Slider>()?.let { adapter.setList(it) }
//        }
//                adapter.notifyDataSetChanged()
//                binding.progressBar.visibility = View.GONE

//        mainActivityViewModel.jsonHome.observe(viewLifecycleOwner, {
//            if (it != null) {
//                it.let { home -> adapter.setList(home) }
//                adapter.notifyDataSetChanged()
//                binding.progressBar.visibility = View.GONE
//            } else {
//                binding.progressBar.visibility = View.GONE
//                Toast.makeText(context, "No data available", Toast.LENGTH_LONG).show()
//            }
//        })
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