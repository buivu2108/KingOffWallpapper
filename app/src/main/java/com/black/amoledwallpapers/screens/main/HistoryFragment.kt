package com.black.amoledwallpapers.screens.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.black.amoledwallpapers.MainViewModel
import com.black.amoledwallpapers.R
import com.black.amoledwallpapers.entities.History
import com.black.amoledwallpapers.screens.image.ImageActivity
import kotlinx.android.synthetic.main.fragment_history.*
import net.idik.lib.slimadapter.SlimAdapter

class HistoryFragment : Fragment() {

    private var viewModel: MainViewModel? = null
    private lateinit var adapter: SlimAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProviders.of(it).get(MainViewModel::class.java) }

        adapter = SlimAdapter.create()
            .register<History>(R.layout.item_image) { data, injector ->
                injector.with<ImageView>(R.id.image) {
//                    ImageLoader.with(it)
//                        .load(ImgurUtil.createThumbUrl(data.url))
//                        .thumbnail(ImageLoader.with(it).load(R.drawable.ripple))
//                        .centerCrop()
//                        .transition(DrawableTransitionOptions.withCrossFade())
//                        .into(it)
                }
                    .clicked(R.id.content) {
                        val list = adapter.data.map { image ->
                            if (image is History) image.id
                            else ""
                        }
                        ImageActivity.open(context, list, data.id)
                    }
            }
            .attachTo(listHistory)
        viewModel?.historyList?.observe(this, Observer {
            adapter.updateData(it)
        })
    }
}