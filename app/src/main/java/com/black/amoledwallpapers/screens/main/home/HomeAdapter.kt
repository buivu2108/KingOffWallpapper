package com.black.amoledwallpapers.screens.main.home


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.black.amoledwallpapers.R
import com.black.amoledwallpapers.entities.Album
import com.black.amoledwallpapers.entities.Image
import com.black.amoledwallpapers.screens.holder.ImageViewHolder
import com.black.amoledwallpapers.screens.main.home.viewholder.CategoriesViewHolder
import com.black.amoledwallpapers.screens.main.home.viewholder.HeaderViewHolder

class HomeAdapter(private val onCategoryClicked: (album: Album) -> Unit, private val onImageClick: (imageId: String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var imageList: List<Image> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var categories: List<Album> = ArrayList()
        set(value) {
            field = value
            notifyItemChanged(1)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> HeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_header_home
                    , parent,
                    false
                )
            )
            1 -> CategoriesViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_categories_home,
                    parent,
                    false
                ), onCategoryClicked
            )
            else -> ImageViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_image,
                    parent,
                    false
                ), onImageClick
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return imageList.size + 2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> holder.bind(imageList[position + 2])
            is CategoriesViewHolder -> holder.bind(categories)
        }
    }
}