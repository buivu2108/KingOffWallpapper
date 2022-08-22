package com.black.amoledwallpapers.screens.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.message.privacy.extensions.doAsync
import com.black.amoledwallpapers.AppRepository
import com.black.amoledwallpapers.entities.Image

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    val imageList: MutableLiveData<List<Image>> = MutableLiveData()
    private val repository = AppRepository.get()

    fun loadAlbum(albumId: Long) {
        doAsync {
            repository.getImages(albumId).let { list ->
                imageList.postValue(list.shuffled())
            }
        }
    }

    fun saveHistory(data: Image) {
        doAsync {
            repository.addHistory(data)
        }
    }
}