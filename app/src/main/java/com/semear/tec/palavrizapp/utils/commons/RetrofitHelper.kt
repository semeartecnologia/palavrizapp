package com.semear.tec.palavrizapp.utils.commons


import android.util.Log
import com.google.gson.GsonBuilder
import com.semear.tec.palavrizapp.BuildConfig
import java.util.*
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.schedulers.Schedulers

object RetrofitHelper {

/*

    fun physio(): PhysioApi {
        val accessToken = "Bearer ${PreferencesHelper.organizationAccessToken}"
        val retrofit = createRetrofitInstance(PhysioConstants.PHYSIO_API_URL, accessToken)
        return retrofit.create(PhysioApi::class.java)
    }

*/


    private fun createRetrofitInstance(url: String, accessToken: String?): Retrofit {
        val builder = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor { message -> Log.d("HttpLoggingInterceptor", message) }
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addNetworkInterceptor(httpLoggingInterceptor)
        }

        accessToken?.let {
            builder.addInterceptor { chain ->
                val request = chain.request().newBuilder().addHeader("Authorization", accessToken).build()
                chain.proceed(request)
            }
        }

        val client = builder.build()
        val gsonBuilder = GsonBuilder()
                .serializeNulls()
                .excludeFieldsWithoutExposeAnnotation()
        return Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .client(client)
                .build()
    }

}
