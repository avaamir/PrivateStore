package com.amir.ir.privatestore.ui.fragments.login

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.LayoutConfirmCodeFragmentBinding
import com.amir.ir.privatestore.utils.Constants
import com.amir.ir.privatestore.utils.millisToTimeString
import com.amir.ir.privatestore.utils.snack
import com.amir.ir.privatestore.utils.toast
import com.amir.ir.privatestore.viewmodels.ConfirmCodeSignUpFragmentViewModel
import com.mukesh.OnOtpCompletionListener


class ConfirmCodeSignUpFragment : Fragment() {

    private var isTimerEndsActionsDone = true

    private val otpCompletionListener = OnOtpCompletionListener { otpCode ->
        mBinding.otpView.setOtpCompletionListener(null)
        viewModel.sendOTP(otpCode)
    }


    private lateinit var mBinding: LayoutConfirmCodeFragmentBinding
    private lateinit var viewModel: ConfirmCodeSignUpFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ConfirmCodeSignUpFragmentViewModel::class.java)
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_confirm_code_fragment,
            container,
            false
        )

        val id = arguments?.getInt(BUNDLE_CONFIRM_CODE_FRAGMENT_ID)!! //todo add this to viewModel
        println("debug: confirmCodeFragment $id")
        viewModel.setId(id)

        initViews()
        subscribeObservers()
        return mBinding.root
    }

    private fun initViews() {
        mBinding.ivClose.setOnClickListener {
            toast("خوش آمدید")
            activity?.finish()
        }

        if (!viewModel.isTimerStarted)
            startTimer()

        mBinding.otpView.setOtpCompletionListener(otpCompletionListener)
    }

    private fun subscribeObservers() {
        viewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                snack(Constants.SERVER_ERROR, {
                    viewModel.sendOTPAgain()
                }, {
                    mBinding.otpView.setOtpCompletionListener(otpCompletionListener)
                })
            } else {
                if (it.error) {
                    toast(it.errorMsg!!)
                    mBinding.otpView.setOtpCompletionListener(otpCompletionListener)
                } else {
                    activity?.finish()
                }
            }
        })

        viewModel.reSendSMSResponse.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it.error) {
                    toast(it.errorMsg!!)
                } else {
                    toast("درخواست شما ثبت شد. لطفا صبور باشید")
                }
            } else {
                toast(Constants.SERVER_ERROR)
            }
        })
    }

    private fun startTimer() {
        viewModel.isTimerStarted = true
        mBinding.btnReSend.setImageVisibility(false)
        mBinding.btnReSend.setTextVisibility(true)
        object : CountDownTimer(3 * 60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (isResumed)
                    mBinding.btnReSend.setText(millisToTimeString(millisUntilFinished).substring(10))
            }

            override fun onFinish() {
                viewModel.isTimerStarted = false
                if (isResumed)
                    onTimerEndsActions()
                else
                    isTimerEndsActionsDone = false
            }
        }.start()
    }

    private fun onTimerEndsActions() {
        viewModel.isTimerStarted = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(mBinding.btnReSend)
        }
        mBinding.btnReSend.setText("ارسال مجدد")
        mBinding.btnReSend.setImageVisibility(true)
        mBinding.btnReSend.setOnClickListener {
            mBinding.btnReSend.setOnClickListener(null)
            startTimer()
            viewModel.smsOTPAgain()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isTimerEndsActionsDone) {
            isTimerEndsActionsDone = true
            onTimerEndsActions()
        }
    }

    companion object {
        private val BUNDLE_CONFIRM_CODE_FRAGMENT_ID by lazy { "ID" }
        fun newInstance(id: Int) = ConfirmCodeSignUpFragment().apply {
            arguments = Bundle().apply {
                putInt(BUNDLE_CONFIRM_CODE_FRAGMENT_ID, id)
            }
        }
    }

}