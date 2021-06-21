package com.tecqza.gdm.fastindia.data

import android.util.Log
import com.tecqza.gdm.fastindia.model.CheckUpdate
import com.tecqza.gdm.fastindia.model.Response
import com.tecqza.gdm.fastindia.model.StatesCities
import com.tecqza.gdm.fastindia.model.Vendors
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface CustomerService {

    @FormUrlEncoded
    @POST("customer/login")
    fun login(@Field("mobile") mobile: String?): Call<Response?>?


    @GET("vendor/vendorList")
    suspend fun getVendorList(): retrofit2.Response<ArrayList<Vendors>?>?

    @FormUrlEncoded
    @POST("customer/register")
    fun customerRegister(
        @Field("name") name: String?,
        @Field("mobile") mobile: String?,
        @Field("email_id") emailId: String?,
        @Field("address") address: String?,
        @Field("state_id") stateId: String?,
        @Field("city_id") cityId: String?
    ): Call<Response>

    @GET("customer/getCity")
    fun getCities(): Call<List<StatesCities>>

    @GET("customer/getState")
    fun getStates(): Call<List<StatesCities>>

    @GET("customer/appVer")
    fun checkUpdate(): Call<CheckUpdate>


    companion object {

        var BASE_URL = "http://fastindia.app/api/"
        var ASSETS_URL = "http://fastindia.app/uploads/customer/"
        var VENDOR_URL = "http://fastindia.app/uploads/vendor/"

        fun create(): CustomerService {

            val builder = OkHttpClient.Builder()
            val httpLoggingInterceptor = HttpLoggingInterceptor { s: String? -> Log.d("asa", s!!) }

            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(httpLoggingInterceptor)

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .client(builder.build())

                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(CustomerService::class.java)
        }
    }
}
