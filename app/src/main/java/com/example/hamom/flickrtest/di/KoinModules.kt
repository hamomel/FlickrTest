package com.example.hamom.flickrtest.di

import android.content.Context
import com.example.hamom.flickrtest.BASE_URL
import com.example.hamom.flickrtest.data.PhotosRepository
import com.example.hamom.flickrtest.data.local.LocalStorage
import com.example.hamom.flickrtest.data.local.LocalStorageImpl
import com.example.hamom.flickrtest.data.local.LocalStorageImpl.Companion.SEARCH_PREFS
import com.example.hamom.flickrtest.data.remote.FlickrApi
import com.example.hamom.flickrtest.data.remote.model.PhotosResponseMapper
import com.example.hamom.flickrtest.presentation.MainViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

const val IO = "io"
const val MAIN = "main"

val appModule = module {
    single { getOkHttpClient() }
    single { getRetrofit(get()) }
    single { get<Retrofit>().create(FlickrApi::class.java)  }
    single { get<Context>().getSharedPreferences(SEARCH_PREFS, Context.MODE_PRIVATE) }
    single<LocalStorage> { LocalStorageImpl(get()) }
    single { PhotosResponseMapper() }
    single { PhotosRepository(get(), get(), get()) }
    single(name = IO) { Schedulers.io() }
    single(name = MAIN) { AndroidSchedulers.mainThread() }
}

val viewModelModule = module {
    viewModel { MainViewModel(get(), get(IO)) }
}

private fun getRetrofit(client: OkHttpClient) =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

private fun getOkHttpClient() =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()