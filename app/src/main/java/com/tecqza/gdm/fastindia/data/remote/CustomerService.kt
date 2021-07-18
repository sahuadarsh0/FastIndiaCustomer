package com.tecqza.gdm.fastindia.data.remote

import android.util.Log
import com.tecqza.gdm.fastindia.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface CustomerService {

    @FormUrlEncoded
    @POST("customer/login")
    fun login(@Field("mobile") mobile: String?): Call<LoginResponse?>?


    @GET("vendor/vendorList")
    suspend fun getVendorList(): Response<ArrayList<Vendor>?>?

    @GET("vendor/products/{vendorId}")
    suspend fun getProductList(
        @Path("vendorId") vendorId: String?
    ): Response<ArrayList<Product>?>?

    @FormUrlEncoded
    @POST("customer/register")
    fun customerRegister(
        @Field("name") name: String?,
        @Field("mobile") mobile: String?,
        @Field("email_id") emailId: String?,
        @Field("address") address: String?,
        @Field("state_id") stateId: String?,
        @Field("city_id") cityId: String?
    ): Call<LoginResponse>

    @GET("customer/getCity")
    fun getCities(): Call<List<StatesCities>>

    @GET("customer/getState")
    fun getStates(): Call<List<StatesCities>>

    @GET("customer/appVer")
    fun checkUpdate(): Call<CheckUpdate>


    @GET("vendor/catList")
    suspend fun home(): Response<Home>


    companion object {

        var BASE_URL = "http://fastindia.app/api/"
        var CUSTOMER_URL = "http://fastindia.app/uploads/customer/"
        var VENDOR_URL = "http://fastindia.app/uploads/vendor/"
        var PRODUCT_URL = "http://fastindia.app/uploads/product/"
        var SLIDER_URL = "http://fastindia.app/uploads/slider/"

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
