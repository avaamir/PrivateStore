package com.amir.ir.privatestore.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.amir.ir.privatestore.UserConfigs
import com.amir.ir.privatestore.models.Favorite
import com.amir.ir.privatestore.repository.persistance.favoritedb.FavoriteRepo
import com.amir.ir.privatestore.utils.Event

class FavoriteFragmentViewModel(application: Application) : AndroidViewModel(application) {

    init {
        FavoriteRepo.setContext(application)
    }

    val getFavoriteServerResponseMessage: LiveData<Event<String>>
        get() = FavoriteRepo.allFavoriteServerResponseMessage

    private val _favoriteDeleteEvent = MutableLiveData<Favorite>()
    val favoriteDeleteResponse = Transformations.switchMap(_favoriteDeleteEvent) {
        FavoriteRepo.delete(it.pid)
    }

    val taggedProducts: LiveData<List<Favorite>> = Transformations.map(FavoriteRepo.allFavorite) {
        it.mapNotNull { favorite ->
            if (favorite.userId != UserConfigs.userVal!!.id) {
                null
            } else {
                favorite
            }
        }
    }

    fun delete(favorite: Favorite) {
        _favoriteDeleteEvent.value = favorite
    }

}