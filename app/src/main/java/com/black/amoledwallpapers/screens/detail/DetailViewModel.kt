package com.black.amoledwallpapers.screens.detail

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.black.amoledwallpapers.*
import com.black.amoledwallpapers.entities.Album
import com.black.amoledwallpapers.entities.Image
import com.black.amoledwallpapers.util.ImageUtil
import com.black.amoledwallpapers.zionfiledownloader.FILE_TYPE
import com.black.amoledwallpapers.zionfiledownloader.ZionDownloadFactory
import com.message.privacy.extensions.doAsync

class DetailViewModel(application: Application) : AndroidViewModel(application) {


    val image: MutableLiveData<Image> = MutableLiveData()
    val favorite: MutableLiveData<Boolean> = MutableLiveData()
    val album: MutableLiveData<Album> = MutableLiveData()
    val imageList: MutableLiveData<List<Image>> = MutableLiveData()
    val status: MutableLiveData<Int> = MutableLiveData()

    private val repository: AppRepository = AppRepository.get()

    fun loadImage(imageId: String) {
        doAsync {
            val img = repository.getImage(imageId)
            img?.also {
                image.postValue(img)
                album.postValue(repository.getAlbum(img.albumId))
                imageList.postValue(repository.getImages(img.albumId))
                favorite.postValue(repository.getFav(it.id) != null)
            }
        }
    }

    fun download(isSet: Boolean = false, onDownloadSuccess: (path: String?) -> Unit) {

        doAsync {
            image.value?.let {
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

    fun changeFavorite() {
        doAsync {
            image.value?.let {
                val fav = repository.getFav(it.id)
                if (fav == null)
                    repository.addFav(it)
                else repository.removeFav(fav.id)
                favorite.postValue(fav == null)
            }
        }
    }

}