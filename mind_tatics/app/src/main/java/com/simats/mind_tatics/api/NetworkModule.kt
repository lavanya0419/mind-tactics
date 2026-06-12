package com.simats.mind_tatics.api

import android.content.Context
import com.simats.mind_tatics.data.AuthDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    // Live Render Backend:
    // private const val BASE_URL = "https://mind-tactics.onrender.com/"
    
    // Local Flask Backend for Physical Device on the same network:
    private const val BASE_URL = "http://10.157.192.141:5000/"
    
    // Local Flask Backend for Android Emulator:
    // private const val BASE_URL = "http://10.0.2.2:5000/"

    fun provideApiService(context: Context): ApiService {
        val authDataStore = AuthDataStore(context)
        
        val authInterceptor = Interceptor { chain ->
            val token = runBlocking { authDataStore.token.first() }
            val request = chain.request().newBuilder()
            if (token != null) {
                request.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(request.build())
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
