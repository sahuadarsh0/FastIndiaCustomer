package com.tecqza.gdm.fastindia.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tecqza.gdm.fastindia.databinding.FragmentDashboardBinding
import com.tecqza.gdm.fastindia.ui.MainActivityViewModel
import com.tecqza.gdm.fastindia.ui.WebPage
import technited.minds.androidutils.SharedPrefs

class DashboardFragment : Fragment() {

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var _binding: FragmentDashboardBinding? = null
    private lateinit var userSharedPreferences: SharedPrefs
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainActivityViewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        userSharedPreferences = SharedPrefs(requireContext(), "USER")

        binding.apply {
            banner.setOnClickListener { startWeb("https://play.google.com/store/apps/details?id=nic.goi.aarogyasetu&hl=en_IN&gl=US") }
            grocery1.setOnClickListener { startWeb("https://shop.bigbazaar.com/") }
        }

        return binding.root
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