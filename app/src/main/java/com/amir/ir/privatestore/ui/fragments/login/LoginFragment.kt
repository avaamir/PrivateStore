package com.amir.ir.privatestore.ui.fragments.login

import android.app.Activity
import android.graphics.Paint
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.LayoutLoginFragmentBinding
import com.amir.ir.privatestore.ui.activities.Fm
import com.amir.ir.privatestore.utils.Constants
import com.amir.ir.privatestore.utils.snack
import com.amir.ir.privatestore.utils.toast
import com.amir.ir.privatestore.viewmodels.LoginFragmentViewModel

class LoginFragment : Fragment() {
    private lateinit var mBinding: LayoutLoginFragmentBinding
    private lateinit var viewModel: LoginFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(LoginFragmentViewModel::class.java)
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.layout_login_fragment, container, false)
        initViews()
        subscribeObservers()
        return mBinding.root
    }

    private fun initViews() {
        mBinding.ivShowPass.setOnClickListener {
            if (mBinding.etPassword.inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT) {
                mBinding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                mBinding.etPassword.setSelection(mBinding.etPassword.text.length)
                mBinding.ivShowPass.setImageDrawable(
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ic_eye
                    )
                )
            } else if (mBinding.etPassword.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                mBinding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                mBinding.etPassword.setSelection(mBinding.etPassword.text.length)
                mBinding.ivShowPass.setImageDrawable(
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ic_no_eye
                    )
                )
            }
            mBinding.etPassword.typeface = ResourcesCompat.getFont(context!!, R.font.iransans)
        }

        mBinding.btnForgotPass.paintFlags =
            mBinding.btnForgotPass.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        mBinding.btnForgotPass.setOnClickListener {
            Fm.addFragmentToStack(ForgotPasswordFragment())
        }

        mBinding.btnSignUp.setOnClickListener {
            Fm.addFragmentToStack(SignUpFragment())
        }

        mBinding.ivClose.setOnClickListener {
            activity?.finish()
        }

        mBinding.btnLogin.setOnClickListener {
            initRequest()
        }
    }

    private fun subscribeObservers() {
        viewModel.user.observe(viewLifecycleOwner, Observer {
            mBinding.btnLogin.showProgressBar(false)
            if (it == null) {
                snack(Constants.SERVER_ERROR, {
                    initRequest()
                }, {
                    mBinding.btnLogin.setOnClickListener { initRequest() }
                })
            } else {
                if (it.error) {
                    mBinding.btnLogin.setOnClickListener { initRequest() }
                    toast(it.errorMsg!!)
                } else {
                    toast("خوش آمدید")
                    activity?.setResult(Activity.RESULT_OK)
                    activity?.finish()
                }
            }
        })
    }


    private fun initRequest() {
        when {
            /* mBinding.etPhone.text.trim().length < 11 || !mBinding.etPhone.text.toString().startsWith("09") -> {
                 toast("شماره تلفن وارد شده صحیح نمیباشد")
             }
             mBinding.etPassword.text.length < 6 -> {
                 toast("پسورد وارد شده صحیح نمیباشد")
             }*/
            else -> {
                val phone = mBinding.etPhone.text.trim().toString()
                val password = mBinding.etPassword.text.toString()
                mBinding.btnLogin.setOnClickListener(null)
                mBinding.btnLogin.showProgressBar(true)
                viewModel.loginUser(phone, password)
            }
        }
    }

}