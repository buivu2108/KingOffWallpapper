package com.black.amoledwallpapers.screens.main.home.viewholder

import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.black.amoledwallpapers.R
import com.black.amoledwallpapers.entities.Album
import com.black.amoledwallpapers.util.LibGlide
import kotlinx.android.synthetic.main.item_categories_home.view.*
import net.idik.lib.slimadapter.SlimAdapter

class CategoriesViewHolder(itemView: View, onClickListener: (album: Album) -> Unit) : RecyclerView.ViewHolder(itemView) {
    private var adapter: SlimAdapter

    init {
        itemView.listCategories.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
        itemView.listCategories
        adapter = SlimAdapter.create()
            .register<Album>(R.layout.item_album_horizontal) { data, injector ->
                injector.with<TextView>(R.id.tvName) {
                    it.typeface = Typeface.createFromAsset(itemView.context.assets, "KaushanScript-Regular.ttf")
                    it.text = data.title
                }
                    .clicked(R.id.content) {
                        onClickListener.invoke(data)
                    }
                    //  .background(R.id.background, Ariana.drawable(ColorUtil.parseList(data.color)))
                    .with<ImageView>(R.id.ivBackground) {
                        LibGlide.with(itemView)
                            .load(data.image)
                            .centerCrop()
                            .into(it)
                    }
            }
            .attachTo(itemView.listCategories)
    }

    fun bind(albumList: List<Album>) {
        adapter.updateData(albumList)
    }
}