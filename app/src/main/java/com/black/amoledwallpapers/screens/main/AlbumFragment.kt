package com.black.amoledwallpapers.screens.main

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.black.amoledwallpapers.MainViewModel
import com.black.amoledwallpapers.R
import com.black.amoledwallpapers.entities.Album
import com.black.amoledwallpapers.screens.categories.CategoryActivity
import kotlinx.android.synthetic.main.fragment_album.*
import net.idik.lib.slimadapter.SlimAdapter
import com.black.amoledwallpapers.util.LibGlide
class AlbumFragment : Fragment() {

    private lateinit var adapter: SlimAdapter
    private var viewModel: MainViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_album, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProviders.of(it).get(MainViewModel::class.java) }
        ivLoading.visibility = View.VISIBLE
        listAlbum.layoutManager = LinearLayoutManager(context)
        adapter = SlimAdapter.create()
            .register<Album>(R.layout.item_album) { data, injector ->
                injector.with<TextView>(R.id.tvName) {
                    it.typeface = Typeface.createFromAsset(activity?.assets, "KaushanScript-Regular.ttf")
                    it.text = data.title
                }.clicked(R.id.content) {
                    context?.let { it1 -> CategoryActivity.open(it1, data) }
                }
                    //  .background(R.id.background, Ariana.drawable(ColorUtil.parseList(data.color)))
                    .with<ImageView>(R.id.ivBackground) {
                        LibGlide.with(this)
                            .load(data.image)
                            .centerCrop()
                            .into(it)
                    }
            }
            .attachTo(listAlbum)
        viewModel?.albumList?.observe(this, Observer {
            ivLoading.visibility = View.GONE
            if (it.isNotEmpty()) {
                adapter.updateData(it)
            }
        })
    }
}