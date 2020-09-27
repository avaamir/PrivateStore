package com.amir.ir.privatestore.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.amir.ir.privatestore.UserConfigs
import com.amir.ir.privatestore.models.Message
import com.amir.ir.privatestore.models.User
import com.amir.ir.privatestore.repository.persistance.messagedb.MessageRepo
import com.amir.ir.privatestore.utils.Event

class ProfileFragmentViewModel(application: Application) : AndroidViewModel(application) {

    init {
        MessageRepo.setContext(application)
    }

    val user:LiveData<User?> get() = UserConfigs.user

    private val getMessageEvent = MutableLiveData<Event<Any>>()
    val messages = Transformations.switchMap( getMessageEvent) {
        MessageRepo.allMessage
    }

    fun getMessages() {
        getMessageEvent.value = Event(Unit)
    }

    fun delete(message: Message) {
        MessageRepo.delete(message)
    }
}