package com.sms.rupeek

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.sms.rupeek.data.WeatherData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import java.util.concurrent.TimeUnit

interface WeatherApi {

    //function to define the header, request method and response callback
    @GET("/v2/5d3a99ed2f0000bac16ec13a/")
    fun weatherSummary(): Call<WeatherData>


    companion object {
        val instance:WeatherApi by lazy {
            //initialize the http builder
            val okhttpClientBuilder = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)

            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY

            val interceptor = Interceptor{chain ->// interceptor to build request and url
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                val response = chain.proceed(request)

                return@Interceptor response
            }

            okhttpClientBuilder.addInterceptor(logging)// add the interceptor to the http builder
            okhttpClientBuilder.addInterceptor(interceptor)

            val okHttpClient =okhttpClientBuilder.build()

            //gson to convert the data to json and viceversa
            val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .setLenient()
                .create()
            Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://www.mocky.io/")//url to get the required data
                .addConverterFactory(GsonConverterFactory.create(gson)).addCallAdapterFactory(
                    CoroutineCallAdapterFactory()
                )
                .build()
                .create(WeatherApi::class.java)

        }
    }
}