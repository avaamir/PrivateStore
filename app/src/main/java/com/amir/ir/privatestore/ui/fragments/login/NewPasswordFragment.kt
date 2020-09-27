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
import com.amir.ir.privatestore.databinding.LayoutNewPasswordFragmentBinding
import com.amir.ir.privatestore.utils.Constants
import com.amir.ir.privatestore.utils.snack
import com.amir.ir.privatestore.utils.toast
import com.amir.ir.privatestore.viewmodels.NewPasswordFragmentViewModel

class NewPasswordFragment : Fragment() {

    private lateinit var mBinding: LayoutNewPasswordFragmentBinding
    private lateinit var viewModel: NewPasswordFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(NewPasswordFragmentViewModel::class.java)
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_new_password_fragment,
            container,
            false
        )

        initViews()
        subscribeObservers()
        return mBinding.root
    }

    private fun subscribeObservers() {
        viewModel.changePasswordResponse.observe(viewLifecycleOwner, Observer {
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
                    toast("پسورد شما تغییر یافت")
                    activity?.finish()
                }
            }
        })

    }

    private fun initRequest() {
        val password = mBinding.etPassword.text.toString().trim()
        val cPassword = mBinding.etConfirmPassword.text.toString().trim()
        if (password != cPassword) {
            toast("پسورد و تکرار آن یکسان نمیباشد")
            return
        }
        if (password.length < 6) {
            toast("پسورد حداقل شش رقمی میباشد")
            return
        }
        mBinding.btnContinue.setOnClickListener(null)
        mBinding.btnContinue.showProgressBar(true)
        viewModel.changePassword(password)
    }

    private fun initViews() {
        mBinding.ivClose.setOnClickListener {
            activity?.finish()
        }

        mBinding.btnContinue.setOnClickListener {
            initRequest()
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


    companion object {
        fun newInstance() = NewPasswordFragment().apply {
            /*arguments = Bundle().apply {
                putString(BUNDLE_NEW_PASSWORD_FRAGMENT_PHONE, phone)
            }*/
        }
    }

}