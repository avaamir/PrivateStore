package com.amir.ir.privatestore

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amir.ir.privatestore.models.User
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.repository.persistance.userdb.UserRepo
import com.amir.ir.privatestore.repository.sharedprefrence.PrefsRepo

object UserConfigs {

    private lateinit var context: Context


    var userVal: User? = null
        private set

    private val userLive = MutableLiveData<User?>(null)
    val user: LiveData<User?> = userLive


    val isLoggedIn get() = userVal != null

    fun setContext(context: Context) {
        if (!this::context.isInitialized) {
            this.context = context.applicationContext
            UserRepo.setContext(context)
            PrefsRepo.setContext(UserConfigs.context)

            UserRepo.users.observeForever { users ->
                if (users.isNotEmpty()) {
                    val user = users[0]
                    println("debug: UserConfigs: $user")
                    ApiService.setBearerToken(user.token!!)
                    userLive.value = user
                    userVal = user //todo mishe hazfesh kard?
                }
            }
        }
    }

    fun loginUser(user: User) {
        if (this.user.value != user) {
            UserRepo.insert(user)
        }
    }

    fun updateUser(
        phone: String = userVal!!.phone,
        name: String = userVal!!.name,
        image: String? = userVal!!.profilePic
    ) {
        UserRepo.update(userVal!!.copy(phone = phone, name = name, profilePic = image))
    }

    fun logout() {
        PrefsRepo.flush()
        //todo delete other databases except favorite
        userVal?.let {
            UserRepo.delete(it)
        }
        userVal = null
        userLive.postValue(null)
    }


}