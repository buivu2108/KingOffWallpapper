package com.black.amoledwallpapers.screens.detail

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.black.amoledwallpapers.App
import com.black.amoledwallpapers.Const
import com.black.amoledwallpapers.R
import com.black.amoledwallpapers.screens.image.ImageActivity
import com.black.amoledwallpapers.util.ImageUtil
import com.black.amoledwallpapers.util.ImgurUtil
import com.black.amoledwallpapers.util.SetWallpaper
import com.black.amoledwallpapers.util.Utils
import com.black.amoledwallpapers.widgets.LoadingDialog
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_image.*

class DetailActivity : AppCompatActivity() {

    private lateinit var viewModel: DetailViewModel
    private lateinit var adapter: DetailAdapter
    private var progressDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        viewModel = ViewModelProviders.of(this)[DetailViewModel::class.java]

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.transparent))
        collapsingToolbar.setCollapsedTitleTextColor(resources.getColor(R.color.colorTextPrimary))

        intent?.getStringExtra("id")?.let {
            viewModel.loadImage(it)
        }

        listImage.layoutManager = GridLayoutManager(this, 2)
        adapter = DetailAdapter {
            Utils.showAdFullScreen(this) {
                ImageActivity.open(this, adapter.list.map { img -> img.id }, it.id)
            }
        }

        listImage.adapter = adapter

        viewModel.image.observe(this, Observer {
            loadingView.visibility = View.VISIBLE
            loadingView.start()
            ImageUtil.loadImage(ImgurUtil.createUrl(it.url), Color.parseColor(it.color), ivImage) {
                loadingView.visibility = View.GONE
            }
            ivImage.setOnClickListener { _ ->
                ImageActivity.open(this, listOf(it.id), it.id)
            }
        })

        viewModel.imageList.observe(this, Observer {
            adapter.updateData(it)
        })
        viewModel.album.observe(this, Observer { collapsingToolbar.title = it.title })
        viewModel.favorite.observe(this, Observer { btnFav.isSelected = it })
        viewModel.status.observe(this, Observer {
            when (it) {
                Const.START -> showLoading()
                Const.DOWNLOAD_COMPLETE,
                Const.ERROR -> {
                    hideLoading()
                }

            }
        })
        btnFav.setOnClickListener {
            viewModel.changeFavorite()
        }
        btnDownload.setOnClickListener {
            downloadFile(false)
        }
        btnApply.setOnClickListener {
            downloadFile()
        }
    }

    fun downloadFile(isSet: Boolean = true) {
        AndPermission.with(this)
            .runtime()
            .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
            .onGranted {
                Utils.showAdFullScreen(this) {
                    viewModel.download(isSet) {path ->
                        SetWallpaper.setWallpaper(
                            this,
                            path,
                            App.get().packageName + ".fileprovider"
                        )
                    }
                }
            }
            .onDenied {
                showSettingsDialog()
            }
            .start()
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature.\nYou can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        builder.show()

    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }


    private fun showLoading() {
        progressDialog = LoadingDialog(this)
        progressDialog?.show()
    }

    private fun hideLoading() {
        progressDialog?.dismiss()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun open(context: Context, imageId: String) {
            context.startActivity(Intent(context, DetailActivity::class.java).apply {
                putExtra("id", imageId)
            })
        }
    }
}