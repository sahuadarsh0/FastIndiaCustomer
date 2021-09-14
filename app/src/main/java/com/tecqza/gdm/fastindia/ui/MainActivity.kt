package com.tecqza.gdm.fastindia.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tecqza.gdm.fastindia.R
import com.tecqza.gdm.fastindia.databinding.ActivityMainBinding
import technited.minds.androidutils.SharedPrefs

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userSharedPreferences: SharedPrefs
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.logout -> {
                    MaterialDialog(this).show {
                        title(text = "Logout?")
                        message(text = "Are you sure want to logout ?")
                        cornerRadius(16f)
                        positiveButton(text = "Yes") { dialog ->
                            userSharedPreferences = SharedPrefs(this@MainActivity, "USER")
                            userSharedPreferences.clearAll()
                            dialog.dismiss()
                            finish()
                        }
                        negativeButton(text = "Cancel") { dialog ->
                            dialog.dismiss()
                        }
                    }
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_dashboard -> {
                    navController.navigate(R.id.navigation_dashboard)
                    return@setOnItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    navController.navigate(R.id.navigation_profile)
                    return@setOnItemSelectedListener true
                }
                else -> Toast.makeText(this, "Select an item", Toast.LENGTH_SHORT).show()
            }
            false
        }

    }
}