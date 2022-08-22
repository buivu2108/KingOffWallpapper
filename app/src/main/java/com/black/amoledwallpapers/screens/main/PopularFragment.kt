package com.black.amoledwallpapers.screens.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.black.amoledwallpapers.MainViewModel
import com.black.amoledwallpapers.R
import com.black.amoledwallpapers.entities.Image
import com.black.amoledwallpapers.screens.detail.DetailActivity
import com.black.amoledwallpapers.util.ImageUtil
import com.black.amoledwallpapers.util.ImgurUtil
import com.black.amoledwallpapers.widgets.WhirlLoadingView
import kotlinx.android.synthetic.main.fragment_popular.*
import net.idik.lib.slimadapter.SlimAdapter

class PopularFragment : Fragment() {
    private var viewModel: MainViewModel? = null
    private lateinit var adapter: SlimAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_popular, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProviders.of(it).get(MainViewModel::class.java) }
        listPopular.layoutManager = GridLayoutManager(context, 2)
        adapter = SlimAdapter.create()
            .register<Image>(R.layout.item_image) { data, injector ->
                injector.with<ImageView>(R.id.image) {
                    injector.with<WhirlLoadingView>(R.id.loadingView) { view ->
                        view.visibility = View.VISIBLE
                        view.start()
                    }
                    ImageUtil.loadImage(
                        url = ImgurUtil.createThumbUrl(data.thumb),
                        color = Color.parseColor(data.color),
                        imageView = it
                    ) { injector.gone(R.id.loadingView) }
                }.clicked(R.id.content) {
                    context?.let { it1 -> DetailActivity.open(it1, data.id) }
                }
            }
            .attachTo(listPopular)

        viewModel?.popularList?.observe(viewLifecycleOwner, Observer { adapter.updateData(it) })
    }
}