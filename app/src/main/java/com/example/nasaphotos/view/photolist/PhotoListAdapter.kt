package com.example.nasaphotos.view.photolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nasaphotos.databinding.CardPhotoItemBinding
import com.example.nasaphotos.model.PhotosItem

class PhotoListAdapter : RecyclerView.Adapter<PhotoListAdapter.PhotoListViewModel>() {
    lateinit var photoCardBinding: CardPhotoItemBinding

    var photoList: MutableList<PhotosItem?> = mutableListOf()
    private var addPhotoClickListener: ((PhotosItem?) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotoListAdapter.PhotoListViewModel {
        photoCardBinding =
            CardPhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoListViewModel(photoCardBinding)
    }

    override fun onBindViewHolder(holder: PhotoListAdapter.PhotoListViewModel, position: Int) {
        holder.bindItem(photoList[position], addPhotoClickListener)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    inner class PhotoListViewModel(private val cardBinding: CardPhotoItemBinding) :
        RecyclerView.ViewHolder(cardBinding.root) {
        fun bindItem(photoItem: PhotosItem?, addPhotoClickListener: ((PhotosItem?) -> Unit)?) {
            Glide.with(cardBinding.root.context)
                .load((photoItem?.imgSrc)?.replace("http:", "https:"))
                .into(cardBinding.imgMain)

            cardBinding.imgMain.setOnClickListener {
                addPhotoClickListener?.invoke(photoItem)
            }
        }
    }

    fun addToList(newList: List<PhotosItem?>) {
        photoList.addAll(newList)
        notifyDataSetChanged()
    }

    fun clearList() {
        photoList.clear()
        notifyDataSetChanged()
    }

    fun setOnPhotoClickListener(onItemClickListener: ((PhotosItem?) -> Unit)?) {
        this.addPhotoClickListener = onItemClickListener
    }

}