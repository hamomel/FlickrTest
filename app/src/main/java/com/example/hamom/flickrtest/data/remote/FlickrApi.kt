package com.example.hamom.flickrtest.data.remote

import com.example.hamom.flickrtest.data.remote.model.PhotosResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {

    @GET("?method=flickr.photos.search&nojsoncallback=1&format=json")
    fun searchPhotos(
        @Query("text") text: String,
        @Query("page") page: Int): Observable<PhotosResponse>

    @GET("?method=flickr.photos.getRecent&nojsoncallback=1&format=json")
    fun getRecentPhotos(@Query("page") page: Int): Observable<PhotosResponse>
}