package com.amir.ir.privatestore.repository.persistance.messagedb

import android.content.Context
import androidx.lifecycle.LiveData
import com.amir.ir.privatestore.models.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


object MessageRepo {
    private lateinit var job: Job
    private lateinit var context: Context

    private val MessageDao: MessageDao by lazy {
        MessageDatabase.getInstance(context).getDao()
    }


    fun setContext(context: Context) {
        this.context = context.applicationContext
    }

    val allMessage: LiveData<List<Message>> by lazy {
        MessageDao.allMessage
    }

    fun insert(items: List<Message>) {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            MessageDao.insertAll(items)
        }
    }

    fun insert(item: Message) {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            MessageDao.insert(item)
        }
    }

    fun update(item: Message) {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            MessageDao.update(item)
        }
    }

    fun delete(item: Message) {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            MessageDao.delete(item)
        }
    }

    fun deleteAllMessages() {
        if (!::job.isInitialized || !job.isActive)
            job = Job()
        CoroutineScope(IO + job).launch {
            MessageDao.deleteAllMessage()
        }
    }

    fun cancelJobs() {
        if (::job.isInitialized && job.isActive)
            job.cancel()
    }
}