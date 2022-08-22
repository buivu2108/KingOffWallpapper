package com.black.amoledwallpapers.screens.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.message.privacy.extensions.doAsync
import com.black.amoledwallpapers.AppRepository

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    var searchList: MutableLiveData<List<Any>> = MutableLiveData()

    fun search(key: String) {
        doAsync {
            val map = HashSet<Long>()
            val list = ArrayList<Any>()
            val albumList = AppRepository.get().getAlbum()
//            AppRepository.get().find(key.toLowerCase()).forEach {
//                if (!map.contains(it.albumId)) {
//                    map.add(it.albumId)
//                    list.add(albumList.first { album -> album.id == it.albumId })
//                }
//                list.add(it)
//            }
            searchList.postValue(list)

        }
    }
}