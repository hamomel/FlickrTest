package com.example.hamom.flickrtest.data

import com.example.hamom.flickrtest.API_KEY
import com.example.hamom.flickrtest.data.local.LocalStorage
import com.example.hamom.flickrtest.data.local.model.Photos
import com.example.hamom.flickrtest.data.remote.FlickrApi
import com.example.hamom.flickrtest.data.remote.model.PhotosResponseMapper
import io.reactivex.Observable

class PhotosRepository(
        private val flickrApi: FlickrApi,
        private val localStorage: LocalStorage,
        private val photosResponseMapper: PhotosResponseMapper
) {

    fun searchPhotos(text: String): Observable<Photos> =
            flickrApi.searchPhotos(API_KEY, text)
                    .doOnSubscribe { localStorage.saveSearchPhrase(text) }
                    .map(photosResponseMapper::map)

    fun getRecentPhotos(): Observable<Photos> =
            flickrApi.getRecentPhotos(API_KEY)
                    .map(photosResponseMapper::map)

    fun getSearchPhrases() = localStorage.getSearchPhrases()
}