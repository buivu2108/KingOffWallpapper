package com.black.amoledwallpapers.screens.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.black.amoledwallpapers.MainViewModel
import com.black.amoledwallpapers.R
import com.black.amoledwallpapers.entities.Album
import com.black.amoledwallpapers.screens.categories.CategoryActivity
import com.black.amoledwallpapers.screens.detail.DetailActivity
import com.black.amoledwallpapers.util.Utils
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private var mainViewModel: MainViewModel? = null
    private lateinit var adapter: HomeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = activity?.let { ViewModelProviders.of(it)[MainViewModel::class.java] }


        listHome.layoutManager = GridLayoutManager(context, 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (position > 1) return 1
                    return 2
                }

            }
        }
        adapter = HomeAdapter(this::onCategoryClick, this::onImageClick)
        listHome.adapter = adapter

        mainViewModel?.homeList?.observe(viewLifecycleOwner, Observer {
            adapter.imageList = it
        })
        mainViewModel?.albumList?.observe(viewLifecycleOwner, Observer {
            adapter.categories = it
        })
    }

    private fun onCategoryClick(album: Album) {
        activity?.let {
            Utils.showAdFullScreen(it) {
                CategoryActivity.open(it, album)
            }
        }
    }

    private fun onImageClick(imageId: String) {
        activity?.let {
            Utils.showAdFullScreen(it) {
                DetailActivity.open(it, imageId)
            }
        }
    }
}