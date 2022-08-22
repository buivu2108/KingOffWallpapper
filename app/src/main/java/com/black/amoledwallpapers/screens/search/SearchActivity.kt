package com.black.amoledwallpapers.screens.search

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
import com.black.amoledwallpapers.util.LibGlide
import com.black.amoledwallpapers.widgets.WhirlLoadingView
import com.wyt.searchbox.SearchFragment
import kotlinx.android.synthetic.main.activity_search.*
import net.idik.lib.slimadapter.SlimAdapter

class SearchActivity : AppCompatActivity() {

    private var viewModel: SearchViewModel? = null
    private lateinit var adapter: SlimAdapter
    private lateinit var searchFragment: SearchFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProviders.of(this)[SearchViewModel::class.java]
        listSearch.layoutManager = GridLayoutManager(this, 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    if (adapter.getItem(position) is Album) return 2
                    return 1
                }

            }
        }

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
                    DetailActivity.open(this, data.id)
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
                        it.typeface = Typeface.createFromAsset(assets, "KaushanScript-Regular.ttf")
                        it.text = data.title
                    }
            }
            .attachTo(listSearch)
        viewModel?.searchList?.observe(this, Observer {
            title = "$title (${it.size})"
            if (it.isEmpty()) {
                llEmpty.visibility = View.VISIBLE
            } else llEmpty.visibility = View.GONE
            adapter.updateData(it)
        })

//        intent?.getStringExtra("key")?.also {
//            title = it
//            viewModel?.search(it)
//        }

        searchFragment = SearchFragment.newInstance()
        searchFragment.setOnSearchClickListener {
//            title = it
//            viewModel?.search(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
            R.id.menu_search -> searchFragment.showFragment(supportFragmentManager, "")
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun search(context: Context, key: String) {
            context.startActivity(Intent(context, SearchActivity::class.java).apply {
                putExtra("key", key)
            })
        }
    }
}