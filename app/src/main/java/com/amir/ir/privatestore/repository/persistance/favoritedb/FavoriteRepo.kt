package com.amir.ir.privatestore.repository.persistance.favoritedb

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amir.ir.privatestore.UserConfigs
import com.amir.ir.privatestore.models.Favorite
import com.amir.ir.privatestore.models.requests.AddFavoriteResponse
import com.amir.ir.privatestore.models.requests.DeleteFavoriteResponse
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.repository.apiservice.launchApi
import com.amir.ir.privatestore.utils.Event
import com.amir.ir.privatestore.utils.RunOnceLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import java.util.concurrent.Executors


object FavoriteRepo {
    private lateinit var job: Job
    private lateinit var context: Context

    val favoriteCoroutineDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val favoriteDao: FavoriteDao by lazy {
        FavoriteDatabase.getInstance(context).getDao()
    }


    fun setContext(context: Context) {
        this.context = context.applicationContext
    }

    private val favoriteServerResponseMessage = MutableLiveData<Event<String>>()
    val allFavoriteServerResponseMessage: LiveData<Event<String>> = favoriteServerResponseMessage

    val allFavorite: LiveData<List<Favorite>>
        get() {
            if (!::job.isInitialized || !job.isActive)
                job = Job()
            CoroutineScope(IO + job).launchApi({
                val response = ApiService.getStoreClient().getFavorites()
                if (response.isSuccessful) {
                    response.body()?.let {
                        if (it.error) {
                            favoriteServerResponseMessage.postValue(Event(it.errorMsg ?: ""))
                        } else {
                            val remoteFavorites = it.products.map { product ->
                                product.makeFavorite(UserConfigs.userVal!!.id)
                            }
                            favoriteDao.insertAll(remoteFavorites)
                        }
                        /*val localFavorites = favoriteDao.allFavorite.value
                        if(localFavorites != null) { //emkan nadarad etefagh bioftad
                            val needToUpdateList = extractAddToServerList(localFavorites, remoteFavorites)
                            ApiService.getStoreClient().getFavorites()
                            favoriteDao.insertAll(remoteFavorites)
                        } else {
                            println("debug: localFavorite is null in FavoriteRepo GetALLFavorites, can not update server this way")
                        }*/
                    }
                } else {
                    favoriteServerResponseMessage.postValue(Event(""))
                }
            }, {
                favoriteServerResponseMessage.postValue(Event(""))
            })
            return favoriteDao.allFavorite
        }

    /*private fun extractAddToServerList(localList: List<Favorite>, remoteList: List<Favorite>) {
        val addToServerList = ArrayList<Favorite>()
        localList.forEach { localItem ->
            var findFlag = false
            remoteList.forEach remoteLoop@{ remoteItem ->
                if (remoteItem.pid == remoteItem.pid) {
                    findFlag = true
                    return@remoteLoop
                }
            }
            if (!findFlag) {
                addToServerList.add(localItem)
            }
        }
    }*/

    fun insert(items: List<Favorite>) {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(favoriteCoroutineDispatcher).launch {
            favoriteDao.insertAll(items)
        }
    }

    fun insertUpdate(item: Favorite): LiveData<AddFavoriteResponse?> {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        return object : RunOnceLiveData<AddFavoriteResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(favoriteCoroutineDispatcher).launchApi({
                    runBlocking {
                        println("debug: scope -> insertUpdate start")
                        val favorite = favoriteDao.exists(item.pid, UserConfigs.userVal!!.id)
                        if (favorite == null) { //insert to local and remote
                            val response = ApiService.getStoreClient().addFavorite(item.pid)
                            if (response.isSuccessful) {
                                favoriteDao.insert(item)
                            }
                            postValue(response.body())
                        } else { //update local
                            favoriteDao.update(item)
                            postValue(AddFavoriteResponse(false, null))
                        }
                        println("debug: scope -> insertUpdate end")
                    }
                }, {
                    postValue(null)
                })
            }

        }
    }

    fun exist(pid: Int): LiveData<Boolean> {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        return object : LiveData<Boolean>() {
            override fun onActive() {
                if (UserConfigs.isLoggedIn) {
                    runBlocking {
                        CoroutineScope(favoriteCoroutineDispatcher).launch {
                            println("debug: scope -> exist start")
                            val favorite = favoriteDao.exists(pid, UserConfigs.userVal!!.id)
                            println("debug: scope -> exist end : ${favorite != null}")
                            postValue(favorite != null)
                        }
                    }
                } else {
                    postValue(false)
                }
            }
        }
    }


    fun delete(pid: Int): LiveData<DeleteFavoriteResponse?> {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        return object : RunOnceLiveData<DeleteFavoriteResponse?>() {
            override fun onActiveRunOnce() {
                CoroutineScope(favoriteCoroutineDispatcher).launchApi({
                    runBlocking {
                        favoriteDao.delete(pid)
                        val response = ApiService.getStoreClient().unTagProduct(pid)
                        postValue(response.body())
                    }
                }, {
                    postValue(null)
                })
            }
        }
    }

    fun deleteAllFavorites() {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(favoriteCoroutineDispatcher).launch {
            favoriteDao.deleteAllFavorite()
        }
    }

    fun cancelJobs() {
        if (::job.isInitialized && job.isActive)
            job.cancel()
    }
}