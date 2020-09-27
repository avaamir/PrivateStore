package com.amir.ir.privatestore.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.amir.ir.privatestore.models.Comment
import com.amir.ir.privatestore.models.requests.GetCommentsResponse
import com.amir.ir.privatestore.models.requests.SubmitCommentRequest
import com.amir.ir.privatestore.models.requests.SubmitCommentResponse
import com.amir.ir.privatestore.repository.RemoteRepo
import com.amir.ir.privatestore.utils.Event


class CommentActivityViewModel : ViewModel() {

    private companion object {
        const val CHUNK_SIZE = 10 //todo ask mamad about this
    }

    private var hasActiveRequest = false
    var isAllPageLoaded = false
        private set
    private val nextPageEvent = MutableLiveData<Event<Any>>()
    private var page = 0
    private var pid = 0


    private val commentList = ArrayList<Comment>()

    val getCommentsResponse: LiveData<GetCommentsResponse?> =
        Transformations.switchMap(nextPageEvent) {
            Transformations.map(RemoteRepo.getComments(pid, page)) { response ->
                var responseToUI: GetCommentsResponse? = null
                if (response != null) {
                    if (!response.error) {
                        commentList.addAll(response.comments)
                        if (response.comments.size < CHUNK_SIZE) {
                            isAllPageLoaded = true
                            println("debug: allPagesLoaded")
                        }
                        responseToUI = response.copy(
                            comments = ArrayList(commentList) // we must take a shallow copy because submitList on adapter don't work when using  reference to previous list //yanni age commentList ro bezanim tu ui tagjiri nemibinim chun submitList kar nemikone
                        )
                    } else {
                        page--
                    }
                } else {
                    page--
                }
                hasActiveRequest = false
                responseToUI
            }
        }


    private val submitCommentRequest = MutableLiveData<SubmitCommentRequest>()
    val submitCommentResponse: LiveData<SubmitCommentResponse?> =
        Transformations.switchMap(submitCommentRequest) { request ->
            RemoteRepo.submitComment(request)
        }


    fun setPid(pid: Int) {
        this.pid = pid
        nextPage()
    }

    fun nextPage() {
        if (!isAllPageLoaded && !hasActiveRequest) {
            hasActiveRequest = true
            page++
            nextPageEvent.value = Event(Unit)
        }
    }

    fun cancelJobs() {
        //todo cancel this specific job
    }

    fun submitComment(commentText: String, rate: Float) {
        submitCommentRequest.value = SubmitCommentRequest(
            pid = pid,
            text = commentText,
            rate = rate
        )
    }

}