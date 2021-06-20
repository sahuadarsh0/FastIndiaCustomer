package com.tecqza.gdm.fastindia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tecqza.gdm.fastindia.databinding.ActivityRegisterBinding
import com.tecqza.gdm.fastindia.model.StatesCities
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import technited.minds.androidutils.SharedPrefs


class RegisterActivity : AppCompatActivity() {
    private val TAG = "Asa"
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var statesList: List<StatesCities>
    private lateinit var citiesList: List<StatesCities>
    val stateIds: MutableList<String> = arrayListOf("")
    val cityIds: MutableList<String> = arrayListOf("")
    private lateinit var stateId: String
    private lateinit var cityId: String
    private lateinit var userSharedPreferences: SharedPrefs
    var isAllFieldsChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getStateAndCity()
        userSharedPreferences = SharedPrefs(this, "USER")

        binding.apply {
            mobileNo.text = intent.getStringExtra("mobile").toString()
            stateSpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                    stateId = stateIds[i + 1]
                    Log.d(TAG, "onItemSelected: $stateId")

                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {

                }
            }
            citySpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                    cityId = cityIds[i + 1]
                    Log.d(TAG, "onItemSelected: $cityId")
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }
            submit.setOnClickListener {
                isAllFieldsChecked = checkAllFields()
                if (isAllFieldsChecked) {
                    register()
                }
            }
        }
    }

    private fun register() {

        val registerCustomer = CustomerService.create().customerRegister(
            binding.name.text.toString(),
            binding.mobileNo.text.toString(),
            binding.emailId.text.toString(),
            binding.address.text.toString(),
            stateId,
            cityId
        )
        registerCustomer.enqueue(object : Callback<com.tecqza.gdm.fastindia.model.Response> {
            override fun onResponse(
                call: Call<com.tecqza.gdm.fastindia.model.Response>,
                response: Response<com.tecqza.gdm.fastindia.model.Response>
            ) {

                if (response.isSuccessful && response.body()?.error.equals("0")) {
                    Toast.makeText(this@RegisterActivity, "Registered", Toast.LENGTH_SHORT).show()
                    binding.apply {
                        userSharedPreferences.set("name", name.text.toString())
                        userSharedPreferences.set("mobile", mobileNo.text.toString())
                        userSharedPreferences.set("emailId", emailId.text.toString())
                        userSharedPreferences.set("address", address.text.toString())
                        userSharedPreferences.set("cityId", cityId)
                        userSharedPreferences.set("stateId", stateId)
                        userSharedPreferences.apply()
                    }
                    openDashboard()
                } else
                    Toast.makeText(this@RegisterActivity, "Error not Registered", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<com.tecqza.gdm.fastindia.model.Response>, t: Throwable) {

            }
        })

    }

    private fun openDashboard() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun checkAllFields(): Boolean {
        binding.apply {
            if (name.text.isNullOrEmpty()) {
                name.error = "Name is required"
                return false
            }
            if (address.text.isNullOrEmpty()) {
                address.error = "Address is required"
                return false
            }
            if (stateId.isEmpty()) {
                return false
            }
            if (cityId.isEmpty()) {
                return false
            }
            if (emailId.text.isNullOrEmpty()) {
                emailId.setText(" ")
            }
        }
        return true
    }


    private fun getStateAndCity() {
        val getState = CustomerService.create().getStates()
        getState.enqueue(object : Callback<List<StatesCities>> {
            override fun onResponse(call: Call<List<StatesCities>>, response: Response<List<StatesCities>>) {
                if (response.isSuccessful) {
                    statesList = response.body()!!
                    val states: MutableList<String> = arrayListOf()
                    statesList.forEach {
                        states.add(it.name!!)
                        stateIds.add(it.stateId!!)
                    }
                    binding.stateSpinner.adapter = ArrayAdapter(
                        this@RegisterActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        states
                    )
                }
            }

            override fun onFailure(call: Call<List<StatesCities>>, t: Throwable) {
            }
        })
        val getCity = CustomerService.create().getCities()
        getCity.enqueue(object : Callback<List<StatesCities>> {

            override fun onResponse(call: Call<List<StatesCities>>, response: Response<List<StatesCities>>) {
                if (response.isSuccessful) {
                    citiesList = response.body()!!
                    val cities: MutableList<String> = arrayListOf()
                    citiesList.forEach {
                        cities.add(it.name!!)
                        cityIds.add(it.cityId!!)
                    }
                    binding.citySpinner.adapter = ArrayAdapter(
                        this@RegisterActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        cities
                    )
                }
            }

            override fun onFailure(call: Call<List<StatesCities>>, t: Throwable) {
            }
        })
    }


}