package com.example.trubbi.services

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private  const val URL = "https://192.168.0.76:3060"
    private val okhttp: OkHttpClient.Builder = OkHttpClient.Builder()
    private val builder: Retrofit.Builder = Retrofit.Builder()
                                            .baseUrl(URL)
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .client(okhttp.build())

    private val retrofit: Retrofit = builder.build()
    fun <T> buildService(serviceType: Class<T>): T{
        return retrofit.create(serviceType)
    }
}