package com.amir.ir.privatestore.repository.persistance.addressdb

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.amir.ir.privatestore.models.Address
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.repository.apiservice.launchApi
import com.amir.ir.privatestore.utils.Event
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


object AddressRepo {
    private lateinit var roomJob: Job
    private lateinit var serverJob: Job
    private lateinit var context: Context

    private var isTodayChecked = false //address haye gerefte shode az server

    private val getAddressesEventLiveData = MutableLiveData<Event<String>>()
    private val insertAddressEventLiveData = MutableLiveData<Event<String>>()
    private val updateAddressEventLiveData = MutableLiveData<Event<String>>()
    private val deleteAddressEventLiveData = MutableLiveData<Event<String>>()

    val getAddressesEvent: LiveData<Event<String>> get() = getAddressesEventLiveData
    val insertAddressEvent: LiveData<Event<String>> get() = insertAddressEventLiveData
    val updateAddressEvent: LiveData<Event<String>> get() = updateAddressEventLiveData
    val deleteAddressEvent: LiveData<Event<String>> get() = deleteAddressEventLiveData

    private val addressDao: AddressDao by lazy {
        AddressDatabase.getInstance(context).getDao()
    }


    fun setContext(context: Context) {
        if (!this::context.isInitialized)
            this.context = context.applicationContext
    }

    val allAddress: LiveData<List<Address>>
        get() {
            return Transformations.map(addressDao.allAddress) { addresses ->
                println("debug: addressRepo: observer called")
                if (!isTodayChecked) {
                    if (!::serverJob.isInitialized || !serverJob.isActive) serverJob = Job()
                    CoroutineScope(IO + serverJob).launchApi({
                        val response = ApiService.getStoreClient().getAddresses()
                        if (response.isSuccessful) {
                            response.body()?.let { getAddressResponse ->
                                if (!getAddressResponse.error) {
                                    isTodayChecked = true
                                    //update db with server addresses
                                    mergeServerWithLocal(addresses, getAddressResponse.addresses)
                                    //
                                    getAddressesEventLiveData.postValue(Event("done"))
                                } else {
                                    if (getAddressResponse.errorMsg != null) {
                                        getAddressesEventLiveData.postValue(Event(getAddressResponse.errorMsg))
                                    }
                                }
                            }
                        } else {
                            getAddressesEventLiveData.postValue(Event(""))
                        }
                    }, {
                        getAddressesEventLiveData.postValue(Event(""))
                    })
                }
                addresses
            }
        }

    private var isInMergeProgress = false

    private suspend fun mergeServerWithLocal(
        localList: List<Address>,
        serverList: List<Address>
    ) {
        println("debug: addressRepo: merge called")
        if (isInMergeProgress) {
            println("debug: addressRepo: merge exit")
            return
        }
        println("debug: addressRepo: merge start")

        println("debug: addressRepo: originalLocal: $localList")
        println("debug: addressRepo: originalRemote: $serverList")

        isInMergeProgress = true

        val serverAddresses = ArrayList(serverList)  //the remains of this list should insert to db
        val updateList = ArrayList<Address>()
        val deleteList = ArrayList<Address>()

        localList.forEach { localAddress ->
            var foundPosition = -1
            serverAddresses.forEachIndexed internalLoop@{ index, remoteAddress ->
                if (localAddress.id == remoteAddress.id) { //found
                    if (localAddress.originalTitle == "بدون عنوان") localAddress.originalTitle = null
                    if (localAddress != remoteAddress) { //should update
                        updateList.add(remoteAddress)
                    }
                    foundPosition = index
                    return@internalLoop
                }
            }
            if (foundPosition != -1) { //if found
                serverAddresses.removeAt(foundPosition)
            } else { //if not found
                deleteList.add(localAddress)
            }
        }
        //Now time to update it
        println("debug: addressRepo: updates: $updateList")
        println("debug: addressRepo: inserts: $serverAddresses")
        println("debug: addressRepo: deletes: $deleteList")
        addressDao.update(updateList)
        addressDao.insertAll(serverAddresses)
        addressDao.delete(deleteList)
        //
        println("debug: addressRepo: merge end")
        isInMergeProgress = false
    }

    fun insert(item: Address) {
        if (!::roomJob.isInitialized || !roomJob.isActive)
            roomJob = Job()
        CoroutineScope(IO + roomJob).launchApi({
            val response = ApiService.getStoreClient().addAddress(item)
            if (response.isSuccessful) {
                response.body()?.let {
                    if (!it.error) {
                        addressDao.insert(item.copy(id = it.id))
                        insertAddressEventLiveData.postValue(Event("done"))
                    } else {
                        if (it.errorMsg != null) {
                            insertAddressEventLiveData.postValue(Event(it.errorMsg))
                        }
                    }
                }
            } else {
                insertAddressEventLiveData.postValue(Event(""))
            }
        }, {
            insertAddressEventLiveData.postValue(Event(""))
        })
    }

    fun update(item: Address) {
        if (!::roomJob.isInitialized || !roomJob.isActive)
            roomJob = Job()
        CoroutineScope(IO + roomJob).launchApi({
            val response = ApiService.getStoreClient().updateAddress(item)
            if (response.isSuccessful) {
                response.body()?.let {
                    if (!it.error) {
                        addressDao.update(item)
                        updateAddressEventLiveData.postValue(Event("done"))
                    } else {
                        if (it.errorMsg != null) {
                            updateAddressEventLiveData.postValue(Event(it.errorMsg))
                        }
                    }
                }
            } else {
                updateAddressEventLiveData.postValue(Event(""))
            }
        }, {
            updateAddressEventLiveData.postValue(Event(""))
        })
    }

    fun delete(item: Address) {
        if (!::roomJob.isInitialized || !roomJob.isActive)
            roomJob = Job()
        CoroutineScope(IO + roomJob).launchApi({
            addressDao.delete(item) //todo in bayad to !it.error bashe vali be khater ke nakhad to UI progressBar bezaram haminja mostaghim pakesh kardam
            val response = ApiService.getStoreClient().deleteAddress(item.id)
            if (response.isSuccessful) {
                response.body()?.let {
                    if (!it.error) {
                        deleteAddressEventLiveData.postValue(Event("done"))
                    } else {
                        if (it.errorMsg != null) {
                            deleteAddressEventLiveData.postValue(Event(it.errorMsg))
                        }
                    }
                }
            } else {
                deleteAddressEventLiveData.postValue(Event(""))
            }
        }, {
            deleteAddressEventLiveData.postValue(Event(""))
        })
    }

    fun insert(items: List<Address>) {
        if (!::roomJob.isInitialized || !roomJob.isActive)
            roomJob = Job()
        CoroutineScope(IO + roomJob).launch {
            addressDao.insertAll(items)
        }
    }

    fun deleteAll() {
        if (!::roomJob.isInitialized || !roomJob.isActive)
            roomJob = Job()
        CoroutineScope(IO + roomJob).launch {
            addressDao.deleteAllAddress()
        }
    }

    fun cancelJobs() {
        if (::roomJob.isInitialized && roomJob.isActive)
            roomJob.cancel()
    }
}