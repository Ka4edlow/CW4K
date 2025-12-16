package com.example.notesapp.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object NetworkModule {
    // Emu: http://10.0.2.2:5000 — заміни на свій хост при реальному девайсі
    private const val BASE_URL = "http://10.0.2.2:5000"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val client = OkHttpClient.Builder().build()

    val api: NotesApi by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(retrofit2.converter.moshi.MoshiConverterFactory.create(moshi))
            .build()
            .create(NotesApi::class.java)
    }
}
