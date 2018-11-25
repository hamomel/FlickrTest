package com.example.hamom.flickrtest.presentation

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hamom.flickrtest.R
import com.example.hamom.flickrtest.data.local.model.Photos.Photo
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_photo.*

class PhotosAdapter(val itemClickListener: (Photo) -> Unit) :
    RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder>() {

    private var photos: MutableList<Photo> = mutableListOf()
    var lastPage = 0
        private set

    fun addData(data: List<Photo>, page: Int) {
        if (page <= lastPage) return
        lastPage = page
        photos.addAll(data)
        notifyDataSetChanged()
    }

    fun reset() {
        photos = mutableListOf()
        lastPage = 0
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, itemType: Int): PhotoViewHolder =
        PhotoViewHolder.create(parent)

    override fun getItemCount() = photos.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position], itemClickListener)
    }

    class PhotoViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        companion object {
            fun create(parent: ViewGroup): PhotoViewHolder =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_photo, parent, false)
                    .let {
                        PhotoViewHolder(it)
                    }
        }

        fun bind(photo: Photo, itemClickListener: (Photo) -> Unit) {
            Picasso.get()
                .load(photo.url)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.photo_placeholder)
                .into(photoView)

            if (photo.title.isNullOrBlank()) {
                titleView.visibility = View.GONE
            } else {
                titleView.text = photo.title
            }

            itemView.setOnClickListener { itemClickListener(photo) }
        }
    }
}