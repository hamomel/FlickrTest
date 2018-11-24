package com.example.hamom.flickrtest.data.remote.model

import com.example.hamom.flickrtest.data.local.model.Photos

class PhotosResponseMapper {

    fun map(response: PhotosResponse): Photos = with(response.photos) {
        Photos(page,
                pages,
                perpage,
                photo.map { mapPhoto(it) },
                total)
    }

    private fun mapPhoto(photo: PhotosResponse.Photo): Photos.Photo = Photos.Photo(photo.constructUrl(), photo.title)

    private fun PhotosResponse.Photo.constructUrl() = "https://farm$farm.staticflickr.com/$server/${id}_$secret.jpg"
}