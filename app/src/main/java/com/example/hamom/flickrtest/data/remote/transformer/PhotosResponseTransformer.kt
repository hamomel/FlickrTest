package com.example.hamom.flickrtest.data.remote.transformer

import com.example.hamom.flickrtest.data.local.model.Photos
import com.example.hamom.flickrtest.data.remote.ApiError
import com.example.hamom.flickrtest.data.remote.model.PhotosResponse
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class PhotosResponseTransformer : ObservableTransformer<PhotosResponse, PhotosResponse> {

    override fun apply(upstream: Observable<PhotosResponse>): ObservableSource<PhotosResponse> =
            upstream.flatMap {
                if (it.stat != "ok") {
                    Observable.error(ApiError(it.message))
                } else {
                    Observable.just(it)
                }
            }
}