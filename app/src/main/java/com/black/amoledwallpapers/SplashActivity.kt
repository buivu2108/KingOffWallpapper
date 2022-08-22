package com.black.amoledwallpapers

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.amoled.ControllMyAd
import com.amoled.blackwallpaper.WallpaperManager
import com.black.amoledwallpapers.ariana.Ariana
import com.black.amoledwallpapers.ariana.GradientAngle
import com.black.amoledwallpapers.entities.Album
import com.black.amoledwallpapers.entities.Image
import com.black.amoledwallpapers.util.ColorUtil
import com.black.amoledwallpapers.util.LibGlide
import com.black.amoledwallpapers.util.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.message.privacy.extensions.doAsync
import com.message.privacy.extensions.onComplete
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {

    private var start: Long = 0
    private var isStart = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        WallpaperManager.start(this)
        setContentView(R.layout.activity_splash)
        start = System.currentTimeMillis()

        LibGlide.with(this)
            .load(R.drawable.main)
            .centerCrop()
            .into(ivBackground)

        background.setBackgroundDrawable(
            Ariana.drawable(
                ColorUtil.parseList("#8a2387#e94057#f27121"),
                GradientAngle.RIGHT_TOP_TO_LEFT_BOTTOM
            )
        )
        initAds()
        doAsync {
            initData()
            onComplete {
                ControllMyAd.getInstance().downloadConfig(
                    this@SplashActivity,
                    ""
                ) {
                    startMain()
                }

            }
        }

    }

    private fun initAds() {
        if (Const.IS_RELEASE) {
            ControllMyAd.getInstance().setUpContext(this@SplashActivity)
                .setUpAdmob(
                    resources.getString(R.string.id_admob_main),
                    resources.getString(R.string.id_admob_banner),
                    resources.getString(R.string.id_admob_full),
                    resources.getString(R.string.id_admob_video), "")
                .setTimeAd("8")
        } else {
            ControllMyAd.getInstance().setUpContext(this@SplashActivity)
                .setIsTest(true)
                .setUpAdmob(
                    resources.getString(R.string.id_admob_main),
                    resources.getString(R.string.id_admob_banner),
                    resources.getString(R.string.id_admob_full),
                    resources.getString(R.string.id_admob_video), "")
                .setTimeAd("8")
        }

        ControllMyAd.getInstance()
            .setUpContext(this@SplashActivity)
            .withAdmobFull()
    }

    private fun startMain() {
        if (System.currentTimeMillis() - start > 2000) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, start + 2000 - System.currentTimeMillis())
        }
    }

    private fun initData() {
        val repository = AppRepository.get()

        var index = 0L
        if (repository.getAlbum().isEmpty()) {
            val text = assets.open("categories.json").bufferedReader()
                .use { it.readText() }
            val albumList: List<Album> = Gson().fromJson(text, object : TypeToken<List<Album>>() {}.type)
            val imageList = ArrayList<Image>()
            albumList.forEach { album ->
                val cat = assets.open("${album.id}.json").bufferedReader()
                    .use { it.readText() }
                val list: List<Image> = Gson().fromJson(cat, object : TypeToken<List<Image>>() {}.type)
                list.forEach { image ->
                    image.albumId = album.id
                    image.url = "${Utils.SERVER_URL}${image.id}.png"
                    image.thumb = "${Utils.SERVER_URL}${image.id}l.png"
                    index++
                }
                imageList.addAll(list)

            }
            repository.insertImage(imageList)
            repository.insertAlbum(albumList)
        }

    }
}