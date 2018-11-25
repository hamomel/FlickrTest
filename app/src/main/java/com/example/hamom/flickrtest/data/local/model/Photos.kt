package com.example.hamom.flickrtest.data.local.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Photos(val page: Int = 0,
                  val pages: Int = 0,
                  val pageSize: Int = 0,
                  val photo: List<Photo> = emptyList(),
                  val total: Int = 0) {
    @Parcelize
    data class Photo(val url: String, val title: String) : Parcelable
}