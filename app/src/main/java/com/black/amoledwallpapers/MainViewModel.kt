package com.black.amoledwallpapers

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.message.privacy.extensions.doAsync
import com.black.amoledwallpapers.entities.Album
import com.black.amoledwallpapers.entities.History
import com.black.amoledwallpapers.entities.Image

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val albumList: MutableLiveData<List<Album>> = MutableLiveData()
    val historyList: LiveData<List<History>>
    val favoriteList: LiveData<List<Any>>
    val homeList: LiveData<List<Image>>
    val popularList: LiveData<List<Image>>

    private val repository: AppRepository = AppRepository.get()

    init {

        historyList = repository.liveHistory()
        favoriteList = Transformations.map(repository.liveFavorite()) {
            ArrayList<Any>().apply {
                if (it.isNotEmpty()) {
                    it.forEachIndexed { index, favorite ->
                        if (index % 10 == 0) {
                            val size = albumList.value?.size ?: 0
                            if (size > 0) {
                                albumList.value?.get(index % size)?.let { it1 -> add(it1) }
                            }
                        }
                        add(favorite)
                    }
                }
            }

        }

        doAsync {
            repository.getAlbum().let { list ->
                albumList.postValue(list)
            }
        }

        homeList = Transformations.map(repository.liveHome()) {
            it.shuffled().filterIndexed { index, _ -> index % 2 == 0 }
        }

        popularList = Transformations.map(repository.liveHome()) {
            it.shuffled().filterIndexed { index, _ -> index % 5 == 0 }
        }

    }
}