package com.amir.ir.privatestore.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.amir.ir.privatestore.UserConfigs
import com.amir.ir.privatestore.models.User
import com.amir.ir.privatestore.repository.RemoteRepo
import com.amir.ir.privatestore.repository.persistance.cartdb.CartRepo
import com.amir.ir.privatestore.repository.sharedprefrence.PrefsRepo
import com.amir.ir.privatestore.utils.Event

class MainActivityViewModel : ViewModel() {

    val user: LiveData<User?> get() = UserConfigs.user
    private var isGetDataCalled = false

    private val shouldRefreshModels: MutableLiveData<Event<Any>> = MutableLiveData()

    val mainPageData = Transformations.switchMap(shouldRefreshModels) {
        RemoteRepo.getMainPage()
    }

    val cartItemsCount: LiveData<Int> = Transformations.map(CartRepo.allCartItem) {
        it.size
    }

    fun refreshData() {
        shouldRefreshModels.value = Event(Unit)
    }


    fun getData() {
        if (!isGetDataCalled) {
            isGetDataCalled = true
            shouldRefreshModels.value = Event(Unit)
        }
    }

    fun cancelJobs() {
        RemoteRepo.cancelServerJobs()
    }

    fun logout() {
        UserConfigs.logout()
    }

    /*fun setUser(user: User?) {
        val temp = this._user.value
        if (temp != user) {
            this._user.value = user
        }
    }*/

    fun shouldShowUpdateMessage(): Boolean {
        val shouldShow = PrefsRepo.shouldUpdateDialogShown()
        if(shouldShow) {
            PrefsRepo.updateDialogShown()
        }
        return shouldShow;
    }

    fun updateUserProfile() {
        //TODO "Not yet implemented"
    }


}