package com.black.amoledwallpapers.screens.detail

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.black.amoledwallpapers.R
import com.black.amoledwallpapers.entities.Image
import com.black.amoledwallpapers.screens.holder.ImageViewHolder
import com.black.amoledwallpapers.util.ImageUtil
import com.black.amoledwallpapers.util.ImgurUtil
import kotlinx.android.synthetic.main.item_image.view.*

class DetailAdapter(private val onClick: (Image) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val list = ArrayList<Image>()

    fun updateData(list: List<Image>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return return ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_image,
                parent,
                false
            )
        ).apply {
            itemView.setOnClickListener { onClick(list[adapterPosition]) }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> holder.bind(list[position])
        }
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(image: Image) {
            itemView.loadingView.color = Color.WHITE
            itemView.loadingView.visibility = View.VISIBLE
            itemView.loadingView.start()
            ImageUtil.loadImage(
                url = ImgurUtil.createThumbUrl(image.thumb),
                color = Color.parseColor(image.color),
                imageView = itemView.image
            ) { itemView.loadingView.visibility = View.GONE }
        }
    }

}