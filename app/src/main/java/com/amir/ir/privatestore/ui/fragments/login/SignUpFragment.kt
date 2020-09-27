package com.amir.ir.privatestore.ui.fragments.login

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
import com.amir.ir.privatestore.databinding.LayoutSignUpFragmentBinding
import com.amir.ir.privatestore.ui.activities.Fm
import com.amir.ir.privatestore.utils.Constants
import com.amir.ir.privatestore.utils.snack
import com.amir.ir.privatestore.utils.toast
import com.amir.ir.privatestore.viewmodels.SignUpFragmentViewModel

class SignUpFragment : Fragment() {

    private lateinit var mBinding: LayoutSignUpFragmentBinding
    private lateinit var viewModel: SignUpFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.layout_sign_up_fragment, container, false)
        viewModel = ViewModelProvider(this).get(SignUpFragmentViewModel::class.java)

        initViews()
        subscribeObservers()
        return mBinding.root
    }

    private fun subscribeObservers() {
        viewModel.signUpResponse.observe(viewLifecycleOwner, Observer {
            mBinding.btnSignUp.showProgressBar(false)
            if (it != null) {
                mBinding.btnSignUp.setOnClickListener { initUser() }
                if (!it.error) {
                    println("debug: SignUpFragment ${it.id}")
                    Fm.addFragmentToStack(
                        ConfirmCodeSignUpFragment.newInstance(
                            id = it.id
                        )
                    )
                } else {
                    mBinding.btnSignUp.setOnClickListener { initUser() }
                    toast(it.errorMsg!!)
                }
            } else {
                mBinding.btnSignUp.setOnClickListener(null)
                snack(Constants.SERVER_ERROR, {
                    initUser() //req to server again
                }, {
                    mBinding.btnSignUp.setOnClickListener { initUser() }
                })

            }
        })
    }


    private fun initViews() {
        mBinding.ivClose.setOnClickListener { activity?.finish() }

        mBinding.btnSignUp.setOnClickListener {
            initUser()
        }

        val onEyeClickListener = View.OnClickListener {
            if (mBinding.etConfirmPassword.inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT) {
                mBinding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                mBinding.etConfirmPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                mBinding.etConfirmPassword.setSelection(mBinding.etConfirmPassword.text.length)
                val drawable = ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_eye
                )
                mBinding.ivShowPass.setImageDrawable(drawable)
                mBinding.ivShowPass2.setImageDrawable(drawable)
            } else if (mBinding.etConfirmPassword.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                mBinding.etConfirmPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                mBinding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                mBinding.etConfirmPassword.setSelection(mBinding.etConfirmPassword.text.length)
                val drawable = ContextCompat.getDrawable(
                    context!!,
                    R.drawable.ic_no_eye
                )
                mBinding.ivShowPass.setImageDrawable(drawable)
                mBinding.ivShowPass2.setImageDrawable(drawable)
            }
            mBinding.etConfirmPassword.typeface = ResourcesCompat.getFont(context!!, R.font.iransans)
            mBinding.etPassword.typeface = ResourcesCompat.getFont(context!!, R.font.iransans)
        }
        mBinding.ivShowPass.setOnClickListener(onEyeClickListener)
        mBinding.ivShowPass2.setOnClickListener(onEyeClickListener)
    }


    private fun initUser() {
        val phone = mBinding.etPhone.text.toString()
        val password = mBinding.etPassword.text.toString()
        val confirmPassword = mBinding.etConfirmPassword.text.toString()
        val name = mBinding.etName.text.toString()

        if (name.length < 6) {
            toast("نام وارد شده قابل قبول نمیباشد")
            return
        }
        if (phone.length < 11 || !phone.startsWith("09")) {
            toast("شماره تلفن وارد شده صحیح نمیباشد")
            return
        }
        if (password.length < 6) {
            toast("پسورد حداقل باید شش رقمی باشد")
            return
        }
        if (password != confirmPassword) {
            toast("پسورد و تکرار آن یکسان نمیباشد")
            return
        }

        mBinding.btnSignUp.showProgressBar(true)
        mBinding.btnSignUp.setOnClickListener(null)

        viewModel.setSignUpInfo(
            name = name,
            phone = phone,
            password = password
        )
    }
}