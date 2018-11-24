package com.example.hamom.flickrtest.presentation

import com.example.hamom.flickrtest.data.local.model.Photos

data class MainViewData(val photos: List<Photos.Photo> = emptyList(),
                        val searchPhrases: Set<String> = emptySet())