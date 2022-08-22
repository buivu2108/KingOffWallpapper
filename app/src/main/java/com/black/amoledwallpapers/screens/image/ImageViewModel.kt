package com.black.amoledwallpapers.screens.image

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.message.privacy.extensions.doAsync
import com.message.privacy.extensions.onComplete
import com.black.amoledwallpapers.*
import com.black.amoledwallpapers.entities.Image
import com.black.amoledwallpapers.util.ImageUtil
import com.black.amoledwallpapers.zionfiledownloader.FILE_TYPE
import com.black.amoledwallpapers.zionfiledownloader.ZionDownloadFactory


class ImageViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var imageList: Array<String>
    val favorite: MutableLiveData<Boolean> = MutableLiveData()
    val status: MutableLiveData<Int> = MutableLiveData()

    private val repository = AppRepository.get()

    fun readData(imageList: Array<String>) {
        this.imageList = imageList
    }

    fun loadImage(id: String, callback: (Image) -> Unit) {
        doAsync {
            val image = repository.getImage(id)
            onComplete {
                image?.let { image -> callback(image) }
            }
        }
    }

    fun checkFavorite(position: Int) {
        doAsync {
            favorite.postValue(repository.getFav(imageList[position]) != null)
        }
    }


    fun download(position: Int, isSet: Boolean = false, onDownloadSuccess: (path: String?) -> Unit) {

        doAsync {
            repository.getImage(imageList[position])?.let {
                status.postValue(Const.START)
                ZionDownloadFactory(getApplication(), it.url, ImageUtil.getFileNameFromUrl(it.url))
                    .downloadFile(FILE_TYPE.IMAGE)
                    .start(onSuccess = { path ->
                        status.postValue(Const.DOWNLOAD_COMPLETE)
                        if (isSet) {
                            onDownloadSuccess.invoke(path)
                        } else {
                            Toast.makeText(App.get(), App.get().getString(R.string.download_ok), Toast.LENGTH_LONG)
                                .show()
                        }
                    }, onFailed = {
                        Toast.makeText(App.get(), App.get().getString(R.string.download_error), Toast.LENGTH_LONG)
                            .show()
                        status.postValue(Const.ERROR)
                    })
            }
        }
    }

    fun changeFavorite(currentItem: Int) {
        doAsync {
            imageList[currentItem].let {
                val fav = repository.getFav(it)
                if (fav == null)
                    repository.getImage(it)?.let { it1 -> repository.addFav(it1) }
                else repository.removeFav(fav.id)
                favorite.postValue(fav == null)
            }
        }
    }

}