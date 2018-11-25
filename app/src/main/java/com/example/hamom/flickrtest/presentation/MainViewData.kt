package com.example.hamom.flickrtest.presentation

import com.example.hamom.flickrtest.data.local.model.Photos

data class MainViewData(val photos: Photos = Photos(),
                        val searchPhrase: String = "",
                        val searchPhrases: Set<String> = emptySet(),
                        val isLoading: Boolean = false)