package com.amir.ir.privatestore.ui.fragments.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.LayoutForgotPasswordFragmentBinding
import com.amir.ir.privatestore.ui.activities.Fm
import com.amir.ir.privatestore.utils.Constants
import com.amir.ir.privatestore.utils.snack
import com.amir.ir.privatestore.utils.toast
import com.amir.ir.privatestore.viewmodels.ForgotPasswordFragmentViewModel

class ForgotPasswordFragment : Fragment() {

    private lateinit var mBinding: LayoutForgotPasswordFragmentBinding
    private lateinit var viewModel: ForgotPasswordFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ForgotPasswordFragmentViewModel::class.java)
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_forgot_password_fragment,
            container,
            false
        )
        initViews()
        subscribeObservers()
        return mBinding.root
    }

    private fun subscribeObservers() {
        viewModel.forgotPassResponse.observe(viewLifecycleOwner, Observer {
            mBinding.btnContinue.showProgressBar(false)
            if (it == null) {
                snack(Constants.SERVER_ERROR, {
                    initRequest()
                }, {
                    mBinding.btnContinue.setOnClickListener { initRequest() }
                })
            } else {
                if (it.error) {
                    mBinding.btnContinue.setOnClickListener { initRequest() }
                    toast(it.errorMsg!!)
                } else {
                    Fm.addFragmentToStack(ConfirmCodeForgotFragment.newInstance(viewModel.phone.value!!))
                }
                mBinding.btnContinue.setOnClickListener { initRequest() }
            }
        })
    }

    private fun initRequest() {
        val phone = mBinding.etPhone.text.toString().trim()
        if (phone.length < 11 || !phone.startsWith("09")) {
            toast("شماره تلفن وارد شده صحیح نمیباشد")
            return
        }
        mBinding.btnContinue.setOnClickListener(null)
        mBinding.btnContinue.showProgressBar(true)
        viewModel.requestNewPassword(phone)
    }


    private fun initViews() {
        mBinding.ivClose.setOnClickListener { activity?.finish() }
        mBinding.btnContinue.setOnClickListener {
           initRequest()
        }
    }

}