package com.tecqza.gdm.fastindia.ui.profile

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tecqza.gdm.fastindia.data.remote.CustomerService
import com.tecqza.gdm.fastindia.databinding.FragmentProfileBinding
import com.tecqza.gdm.fastindia.model.StatesCities
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import technited.minds.androidutils.SharedPrefs

class ProfileFragment : Fragment() {

    private val TAG = "Asa"
    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: FragmentProfileBinding? = null
    private lateinit var userSharedPreferences: SharedPrefs
    private val binding get() = _binding!!
    private lateinit var statesList: List<StatesCities>
    private lateinit var citiesList: List<StatesCities>
    val stateIds: MutableList<String> = arrayListOf("")
    val cityIds: MutableList<String> = arrayListOf("")
    private lateinit var stateId: String
    private lateinit var cityId: String
    var isAllFieldsChecked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        getStateAndCity()

        userSharedPreferences = SharedPrefs(requireContext(), "USER")
        if (!userSharedPreferences["name"].isNullOrEmpty())
            binding.apply {


                stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                        stateId = stateIds[i]
                        Log.d(TAG, "onItemSelected: stateId $stateId")

                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>?) {

                    }
                }
                citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
                        cityId = cityIds[i]
                        adapterView?.getItemAtPosition(i)
                        Log.d(TAG, "onItemSelected: cityId $cityId")
                        Log.d(TAG, "onItemSelected: cityId ${adapterView?.getItemAtPosition(i)}")
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
        return binding.root
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
        registerCustomer.enqueue(object : Callback<com.tecqza.gdm.fastindia.model.LoginResponse> {
            override fun onResponse(
                call: Call<com.tecqza.gdm.fastindia.model.LoginResponse>,
                loginResponse: Response<com.tecqza.gdm.fastindia.model.LoginResponse>
            ) {

                if (loginResponse.isSuccessful && loginResponse.body()?.error.equals("0")) {
                    Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show()
                    binding.apply {
                        userSharedPreferences["name"] = name.text.toString()
                        userSharedPreferences["mobile"] = mobileNo.text.toString()
                        userSharedPreferences["emailId"] = emailId.text.toString()
                        userSharedPreferences["address"] = address.text.toString()
                        userSharedPreferences["cityId"] = cityId
                        userSharedPreferences["stateId"] = stateId
                        userSharedPreferences.apply()
                    }
//                    openDashboard()
                } else
                    Toast.makeText(requireContext(), "Error not Updated", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<com.tecqza.gdm.fastindia.model.LoginResponse>, t: Throwable) {

            }
        })

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
                    stateIds.clear()
                    statesList.forEach {
                        states.add(it.name!!)
                        stateIds.add(it.stateId!!)
                    }
                    binding.stateSpinner.adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.simple_spinner_dropdown_item,
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
                    cityIds.clear()
                    citiesList.forEach {
                        cities.add(it.name!!)
                        cityIds.add(it.cityId!!)
                    }
                    binding.citySpinner.adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.simple_spinner_dropdown_item,
                        cities
                    )
                    initFields()
                }
            }

            override fun onFailure(call: Call<List<StatesCities>>, t: Throwable) {
            }
        })
    }

    private fun initFields() {
        binding.apply {

            with(userSharedPreferences) {
                name.setText(get("name"))
                mobileNo.text = get("mobile")
                emailId.setText(get("emailId"))
                address.setText(get("address"))
                val cityId = get("cityId")!!.toInt()-1
                val stateId = get("stateId")!!.toInt()-1
                citySpinner.setSelection(cityId, true)
                stateSpinner.setSelection(stateId, true)
//                getSpinnerField().getAdapter().indexOf(value);
//                val spinnerPosition: Int = citySpinner.

//                mSpinner.setSelection(spinnerPosition)
//                getSpinnerField().setSelection(pos);
                Log.d(TAG, "onViewCreated: stateId ${get("stateId")!!.toInt()} $stateId ")
                Log.d(TAG, "onViewCreated: cityId ${get("cityId")!!.toInt()}  $cityId")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}