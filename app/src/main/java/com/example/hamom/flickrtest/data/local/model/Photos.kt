package com.example.hamom.flickrtest.data.local.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Photos(val page: Int,
                  val pages: Int,
                  val perpage: Int,
                  val photo: List<Photo>,
                  val total: String) {
    @Parcelize
    data class Photo(val url: String, val description: String) : Parcelable
}