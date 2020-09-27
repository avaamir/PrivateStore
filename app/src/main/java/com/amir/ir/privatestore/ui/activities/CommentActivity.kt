package com.amir.ir.privatestore.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.UserConfigs
import com.amir.ir.privatestore.databinding.ActivityCommentBinding
import com.amir.ir.privatestore.models.Comment
import com.amir.ir.privatestore.models.Product
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.ui.adapters.CommentAdapter
import com.amir.ir.privatestore.ui.dialogs.CommentDialog
import com.amir.ir.privatestore.ui.dialogs.NoNetworkDialog
import com.amir.ir.privatestore.utils.Constants
import com.amir.ir.privatestore.utils.Event
import com.amir.ir.privatestore.utils.snack
import com.amir.ir.privatestore.utils.toast
import com.amir.ir.privatestore.viewmodels.CommentActivityViewModel
import com.google.android.material.snackbar.Snackbar

class CommentActivity : AppCompatActivity(), CommentAdapter.Interaction, ApiService.OnUnauthorizedListener, ApiService.InternetConnectionListener {

    private lateinit var viewModel: CommentActivityViewModel
    private lateinit var mBinding: ActivityCommentBinding
    private lateinit var mAdapter: CommentAdapter

    private var commentDialog: CommentDialog? = null

    private var isFirstRun = true

    private var snackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)


        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_comment)
        viewModel = ViewModelProvider(this).get(CommentActivityViewModel::class.java)

        val product = intent.getParcelableExtra<Product>(Constants.INTENT_COMMENT_ACTIVITY_PRODUCT)


        mBinding.productName = product.name
        viewModel.setPid(product.pid)

        initViews()
        subscribeObservers()
    }

    override fun onBackPressed() {
        viewModel.cancelJobs()
        super.onBackPressed()
    }

    private fun subscribeObservers() {
        viewModel.submitCommentResponse.observe(this, Observer {
            commentDialog!!.showProgress(false) //todo age rotate she momkene az mem leak bede?? chun tu safhe nist dg vali refrence behesh hast
            if (it != null) {
                if (!it.error) {
                    commentDialog!!.dismiss() //todo age rotate she momkene az mem leak bede?? chun tu safhe nist dg vali refrence behesh hast
                    toast("دیدگاه شما ثبت شد و پس از تایید نمایش داده خواهد شد", true)
                } else {
                    toast(it.errorMsg ?: Constants.SERVER_ERROR)
                }
            } else {
                toast(Constants.SERVER_ERROR)
            }
        })

        viewModel.getCommentsResponse.observe(this, Observer { response ->
            if (mBinding.progressBar.visibility == View.VISIBLE) {
                mBinding.progressBar.visibility = View.GONE
            }
            mBinding.frameProgress.visibility = View.GONE
            if (response != null) {
                if (!response.error) {
                    println("debug: size = ${response.comments.size}")
                    println("debug: ${response.comments}")
                    mAdapter.submitList(response.comments)
                    if (isFirstRun) {
                        isFirstRun = false
                        if (mAdapter.currentList.isEmpty()) {
                            println("dloebug: currentList isEmpty")
                            mBinding.tvMessage.visibility = View.VISIBLE
                            mBinding.recyclerComments.visibility = View.GONE
                        } else {
                            println("debug: currentList isNotEmpty")
                            mBinding.recyclerComments.visibility = View.VISIBLE
                        }
                    }
                } else {
                    toast(response.errorMsg ?: Constants.SERVER_ERROR)
                }
            } else {
                snackBar = snack(Constants.SERVER_ERROR) {
                    viewModel.nextPage()
                }
            }
        })
    }

    private fun initViews() {
        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }

        mBinding.btnSubmitComment.setOnClickListener {
            if(UserConfigs.isLoggedIn) {
               showCommentDialog()
            } else {
                startActivityForResult(Intent(this, LoginActivity::class.java), Constants.INTENT_LOGIN_ACTIVITY_REQ_LOGIN)
            }
        }
        mAdapter = CommentAdapter(this)
        mBinding.recyclerComments.adapter = mAdapter
        mBinding.recyclerComments.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("debug: onActResult")
        if (requestCode == Constants.INTENT_LOGIN_ACTIVITY_REQ_LOGIN) {
            println("debug: onActResult INTENT_LOGIN_ACTIVITY_REQ_LOGIN")
            if (resultCode == Activity.RESULT_OK) {
                println("debug: onActResult INTENT_LOGIN_ACTIVITY_REQ_LOGIN : RESULT_OK")
                showCommentDialog()
            }
        }
    }

    private fun showCommentDialog() {
        commentDialog = CommentDialog(
            this,
            R.style.my_dialog_animation
        ) { commentText, rate, dialog ->
            if (commentText.isBlank()) {
                toast("لطفا دیدگاه خود را بنویسید")
            } else {
                viewModel.submitComment(commentText, rate)
                dialog.showProgress(true)
            }
        }
        commentDialog?.show()
    }

    override fun onItemClicked(item: Comment) {
        //todo felan chizi piade sazi nashode, dar feature haye bad ghabeliat pasokh be comment mikhahad
    }

    override fun onListEnd() {
        println("debug: OnListEnd Called")
        snackBar?.let {
            if (it.isShown)
                return@onListEnd
        }
        viewModel.nextPage()
        if (!viewModel.isAllPageLoaded) {
            mBinding.frameProgress.visibility = View.VISIBLE
        }
    }

    override fun onUnauthorizedAction(event: Event<Unit>) {
        toast(Constants.UNAUTHORIZED_MSG)
        commentDialog?.dismiss()
        startActivityForResult(Intent(this, LoginActivity::class.java), Constants.INTENT_LOGIN_ACTIVITY_REQ_LOGIN)
    }

    override fun onInternetUnavailable() {
        NoNetworkDialog(this, R.style.my_alert_dialog).show()
    }
}
