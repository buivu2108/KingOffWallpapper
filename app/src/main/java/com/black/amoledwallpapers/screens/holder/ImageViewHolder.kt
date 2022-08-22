package com.black.amoledwallpapers.screens.holder

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.black.amoledwallpapers.entities.Image
import com.black.amoledwallpapers.util.ImageUtil
import com.black.amoledwallpapers.util.ImgurUtil
import kotlinx.android.synthetic.main.item_image.view.*

class ImageViewHolder(itemView: View, val onItemClick: (imageId: String) -> Unit) : RecyclerView.ViewHolder(itemView) {

    fun bind(image: Image) {
        itemView.loadingView.color = Color.WHITE
        itemView.loadingView.visibility = View.VISIBLE
        itemView.loadingView.start()
        ImageUtil.loadImage(
            url = ImgurUtil.createThumbUrl(image.thumb),
            color = Color.parseColor(image.color),
            imageView = itemView.image
        ) { itemView.loadingView.visibility = View.GONE }
        itemView.setOnClickListener {
            onItemClick.invoke(image.id)
        }
    }
}