package com.black.amoledwallpapers.screens.main

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.black.amoledwallpapers.MainViewModel
import com.black.amoledwallpapers.R
import com.black.amoledwallpapers.entities.Album
import com.black.amoledwallpapers.entities.Favorite
import com.black.amoledwallpapers.screens.image.ImageActivity
import com.black.amoledwallpapers.util.ImageUtil
import com.black.amoledwallpapers.util.ImgurUtil
import com.black.amoledwallpapers.util.LibGlide
import com.black.amoledwallpapers.widgets.WhirlLoadingView
import kotlinx.android.synthetic.main.fragment_history.*
import net.idik.lib.slimadapter.SlimAdapter

class FavoriteFragment : Fragment() {

    private var viewModel: MainViewModel? = null
    private lateinit var adapter: SlimAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProviders.of(it).get(MainViewModel::class.java) }

        listHistory.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        adapter = SlimAdapter.create()
            .register<Favorite>(R.layout.item_image) { data, injector ->
                injector.with<ImageView>(R.id.image) {
                    injector.with<WhirlLoadingView>(R.id.loadingView) { view ->
                        view.visibility = View.VISIBLE
                        view.start()
                    }
                    ImageUtil.loadImage(
                        url = ImgurUtil.createThumbUrl(data.url),
                        color = Color.parseColor(data.color),
                        imageView = it
                    ) { injector.gone(R.id.loadingView) }
                }.clicked(R.id.content) {
                    val list = adapter.data.map { image ->
                        if (image is Favorite) image.id
                        else ""
                    }
                    ImageActivity.open(context, list, data.id)
                }
            }
            .register<Album>(R.layout.item_album_small) { data, injector ->
                injector.with<ImageView>(R.id.image) {
                    LibGlide.with(it)
                        .load(ImgurUtil.createThumbUrl(data.image))
                        .centerCrop()
                        .into(it)
                }
                    // .background(R.id.background, Ariana.drawable(ColorUtil.parseList(data.color)))
                    .with<TextView>(R.id.tvName) {
                        it.typeface = Typeface.createFromAsset(activity?.assets, "KaushanScript-Regular.ttf")
                        it.text = data.title
                    }
            }
            .attachTo(listHistory)
        viewModel?.favoriteList?.observe(this, Observer {
            if (it.isEmpty()) {
                llEmpty.visibility = View.VISIBLE
            } else llEmpty.visibility = View.GONE
            adapter.updateData(it)
        })
    }
}