package com.example.hamom.flickrtest.presentation

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hamom.flickrtest.R
import com.example.hamom.flickrtest.data.local.model.Photos
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_photo.*

class PhotosAdapter(val itemClickListener: (Photos.Photo) -> Unit) :
    RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder>() {

    private lateinit var conext: Context

    var photos: List<Photos.Photo> = emptyList()
        set(value) {
            if (value != field) {
                field = value
                notifyDataSetChanged()
            }
        }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        conext = recyclerView.context
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

        fun bind(photo: Photos.Photo, itemClickListener: (Photos.Photo) -> Unit) {
            Picasso.get()
                .load(photo.url)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.photo_placeholder)
                .into(photoView)

            if (photo.description.isNullOrBlank()) {
                titleView.visibility = View.GONE
            } else {
                titleView.text = photo.description
            }

            itemView.setOnClickListener { itemClickListener(photo) }
        }
    }
}