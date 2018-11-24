package com.example.hamom.flickrtest.data.remote.model

import com.google.gson.annotations.SerializedName

data class PhotosResponse(
    @SerializedName("photos")
    val photos: Photos,
    @SerializedName("stat")
    val stat: String
) {

    data class Photos(
        @SerializedName("page")
        val page: Int,
        @SerializedName("pages")
        val pages: Int,
        @SerializedName("perpage")
        val perpage: Int,
        @SerializedName("photo")
        val photo: List<Photo>,
        @SerializedName("total")
        val total: String
    )

    data class Photo(
        @SerializedName("farm")
        val farm: Int,
        @SerializedName("id")
        val id: String,
        @SerializedName("isfamily")
        val isfamily: Int,
        @SerializedName("isfriend")
        val isfriend: Int,
        @SerializedName("ispublic")
        val ispublic: Int,
        @SerializedName("owner")
        val owner: String,
        @SerializedName("secret")
        val secret: String,
        @SerializedName("server")
        val server: String,
        @SerializedName("title")
        val title: String
    )
}