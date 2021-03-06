package com.tecqza.gdm.fastindia.ui

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tecqza.gdm.fastindia.data.remote.CustomerService
import com.tecqza.gdm.fastindia.databinding.ActivityLoginBinding
import com.tecqza.gdm.fastindia.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import technited.minds.androidutils.ProcessDialog
import technited.minds.androidutils.SharedPrefs


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var otp: String
    private lateinit var loginResponseData: LoginResponse
    private lateinit var userSharedPreferences: SharedPrefs
    private lateinit var processDialog: ProcessDialog
    private lateinit var transfer: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        processDialog = ProcessDialog(this)
        userSharedPreferences = SharedPrefs(this, "USER")

        if (!userSharedPreferences["name"].isNullOrEmpty()) {
            openDashboard()
        }
        binding.apply {
            sendOtp.setOnClickListener {
                if (mobile.text.isNotEmpty() || mobile.text.matches(Regex("^[6-9][0-9]{9}$"))) {
                    send(binding.mobile.text.toString())
                } else {
                    Toast.makeText(this@LoginActivity, "Enter 10 digit Number", Toast.LENGTH_SHORT).show()
                }
            }
            verify.setOnClickListener {
                if (otp.text.isNotEmpty() || otp.text.matches(Regex("^[0-9]{4}$"))) {
                    verify(otp.text.toString())
                } else {
                    Toast.makeText(this@LoginActivity, "Enter 4 digit OTP", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun send(mobile: String) {
        processDialog.show()
        val apiInterface = CustomerService.create().login(mobile)
        apiInterface?.enqueue(object : Callback<LoginResponse?> {
            override fun onResponse(call: Call<LoginResponse?>, loginResponse: Response<LoginResponse?>) {
                if (loginResponse.body()?.error.equals("0")) {
                    visibleSubmit()
                    loginResponseData = loginResponse.body()!!
                    otp = loginResponse.body()?.otp.toString()
                    transfer = loginResponseData.message.toString()
                    loginResponseData.data?.apply {
                        userSharedPreferences["name"] = name
                        userSharedPreferences["mobile"] = mobile
                        userSharedPreferences["emailId"] = emailId
                        userSharedPreferences["address"] = address
                        userSharedPreferences["cityId"] = cityId
                        userSharedPreferences["stateId"] = stateId
                        userSharedPreferences.apply()
                    }
                }
                processDialog.dismiss()
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                Toast.makeText(applicationContext, "Invalid Mobile! OTP not sent", Toast.LENGTH_SHORT).show()
                processDialog.dismiss()
            }
        })

    }

    private fun verify(enteredOtp: String) {
        if (enteredOtp == otp) {
            when (transfer) {
                "Login" -> openDashboard()
                "Register" -> openRegister()
            }
        } else {
            Toast.makeText(this, "Incorrect OTP", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openDashboard() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun openRegister() {
        val i = Intent(this, RegisterActivity::class.java)
        i.putExtra("mobile", binding.mobile.text.toString())
        startActivity(i)
        finish()
    }

    private fun visibleSubmit() {
        binding.mobile.visibility = GONE
        binding.sendOtp.visibility = GONE
        binding.otp.visibility = VISIBLE
        binding.verify.visibility = VISIBLE
    }
}