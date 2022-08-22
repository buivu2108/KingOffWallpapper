package com.black.amoledwallpapers.screens.categories

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.black.amoledwallpapers.R
import com.black.amoledwallpapers.entities.Album
import com.black.amoledwallpapers.entities.Image
import com.black.amoledwallpapers.screens.detail.DetailActivity
import com.black.amoledwallpapers.util.ImageUtil
import com.black.amoledwallpapers.util.ImgurUtil
import com.black.amoledwallpapers.util.Utils
import com.black.amoledwallpapers.widgets.WhirlLoadingView
import kotlinx.android.synthetic.main.activity_category.*
import net.idik.lib.slimadapter.SlimAdapter


class CategoryActivity : AppCompatActivity() {

    private lateinit var viewModel: CategoryViewModel
    private lateinit var adapter: SlimAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        setSupportActionBar(toolbar)
        viewModel = ViewModelProviders.of(this).get(CategoryViewModel::class.java)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        ivLoading.visibility = View.VISIBLE

        listWallpaper.layoutManager = GridLayoutManager(this, 2)
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
                    Utils.showAdFullScreen(this) {
                        viewModel.saveHistory(data)
                        DetailActivity.open(this@CategoryActivity, data.id)
                    }
                }
            }
            .attachTo(listWallpaper)

        viewModel.imageList.observe(this, Observer {
            if (it.isNotEmpty()) {
                ivLoading.visibility = View.GONE
                adapter.updateData(it)
            }
        })

        intent.getParcelableExtra<Album>(PARAMS_ALBUM)?.let {
            viewModel.loadAlbum(it.id)
            title = it.title
        }

        //  ivBack.setOnClickListener { finish() }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val PARAMS_ALBUM = "album"

        fun open(context: Context, album: Album) {
            val data = Intent(context, CategoryActivity::class.java)
            data.putExtra(PARAMS_ALBUM, album)
            context.startActivity(data)
        }
    }
}