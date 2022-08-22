package com.black.amoledwallpapers.screens.image

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.black.amoledwallpapers.App
import com.black.amoledwallpapers.Const
import com.black.amoledwallpapers.R
import com.black.amoledwallpapers.util.NetworkUtil
import com.black.amoledwallpapers.util.SetWallpaper
import com.black.amoledwallpapers.util.Utils
import com.black.amoledwallpapers.widgets.LoadingDialog
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import kotlinx.android.synthetic.main.activity_image.*


class ImageActivity : AppCompatActivity() {

    private lateinit var viewModel: ImageViewModel

    private var progressDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        viewModel = ViewModelProviders.of(this).get(ImageViewModel::class.java)

        viewModel.favorite.observe(this, Observer { ivFav.isSelected = it })

        ivFav.setOnClickListener { viewModel.changeFavorite(vpImage.currentItem) }
        ivDownload.setOnClickListener {
            download(false)
        }
        tvApply.setOnClickListener {
            download(true)
        }

        vpImage.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                viewModel.checkFavorite(position)
            }

        })

        intent?.getStringArrayListExtra(PARAMS_LIST)?.let {
            if (it.isNotEmpty()) {
                viewModel.readData(it.toTypedArray())
                vpImage.adapter = ImageAdapter(supportFragmentManager, it)
                vpImage.currentItem = intent?.getIntExtra(PARAMS_POSITION, 0) ?: 0
                if (vpImage.currentItem == 0) {
                    viewModel.checkFavorite(0)
                }
            }
        }

        viewModel.status.observe(this, Observer {
            when (it) {
                Const.START -> showLoading()
                Const.DOWNLOAD_COMPLETE,
                Const.ERROR -> {
                    hideLoading()
                }

            }
        })

    }

    private fun showLoading() {
        progressDialog = LoadingDialog(this)
        progressDialog?.show()
    }

    private fun hideLoading() {
        progressDialog?.dismiss()
        setFullScreen()
    }

    private fun download(isSet: Boolean) {
        if (!NetworkUtil.isConnected(this)) {
            showDialogWarning()
            return
        }
        Utils.showAdFullScreen(this) {
            AndPermission.with(this)
                .runtime()
                .permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted {
                    viewModel.download(vpImage.currentItem, isSet) { path ->
                        SetWallpaper.setWallpaper(this, path, App.get().packageName + ".fileprovider")
                    }
                }
                .onDenied {
                    showSettingsDialog()
                }
                .start()
        }
    }

    override fun onResume() {
        super.onResume()
        setFullScreen()
    }

    private fun setFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
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

    private fun showDialogWarning() {
        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage("No internet connection.\nMake sure Wifi or cellular data is turned on, then try again!")
        builder1.setCancelable(true)

        builder1.setPositiveButton("Yes") { dialog, id -> dialog.cancel() }
        val alert11 = builder1.create()
        alert11.show()

    }

    class ImageAdapter(fm: FragmentManager, private val imageIdList: List<String>) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return ImageFragment.newInstance(imageIdList[position])
        }

        override fun getCount(): Int = imageIdList.size

    }


    companion object {
        const val PARAMS_LIST = "list"
        const val PARAMS_POSITION = "position"
        fun open(context: Context?, imageList: List<String>, imageId: String) {
            val intent = Intent(context, ImageActivity::class.java).apply {
                putStringArrayListExtra(PARAMS_LIST, ArrayList(imageList))
                putExtra(PARAMS_POSITION, imageList.indexOf(imageId))
            }
            context?.startActivity(intent)
        }
    }
}