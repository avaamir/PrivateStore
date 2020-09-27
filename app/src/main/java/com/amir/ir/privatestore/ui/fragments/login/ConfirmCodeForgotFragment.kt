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
import com.amir.ir.privatestore.ui.activities.Fm
import com.amir.ir.privatestore.utils.Constants
import com.amir.ir.privatestore.utils.millisToTimeString
import com.amir.ir.privatestore.utils.snack
import com.amir.ir.privatestore.utils.toast
import com.amir.ir.privatestore.viewmodels.ConfirmCodeForgotFragmentViewModel
import com.mukesh.OnOtpCompletionListener

class ConfirmCodeForgotFragment : Fragment() {


    private var isTimerEndsActionDone = true

    private val otpCompletionListener = OnOtpCompletionListener { otpCode ->
        mBinding.otpView.setOtpCompletionListener(null)
        viewModel.sendOTP(otpCode)
    }

    private lateinit var mBinding: LayoutConfirmCodeFragmentBinding
    private lateinit var viewModel: ConfirmCodeForgotFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ConfirmCodeForgotFragmentViewModel::class.java)
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_confirm_code_fragment,
            container,
            false
        )

        //todo server <- phone + otp  : (ConfirmForgotPassCodeReq)
        val phone =
            arguments?.getString(BUNDLE_CONFIRM_CODE_FRAGMENT_PHONE) //todo add this to viewModel
        viewModel.setPhone(phone)

        initViews()
        subscribeObservers()

        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        if (!isTimerEndsActionDone) {
            isTimerEndsActionDone = true
            onTimerEndsAction()
        }
    }

    private fun initViews() {
        mBinding.ivClose.setOnClickListener {
            activity?.finish()
        }

        if (!viewModel.isTimerStarted)
            startTimer() //todo get from view model to start or not

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
                    toast("شما وارد حساب خود شدید. لطفا پسورد جدید را تعیین کنید", true)
                    Fm.addFragmentToStack(NewPasswordFragment.newInstance())
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
                if (isVisible)
                    mBinding.btnReSend.setText(millisToTimeString(millisUntilFinished).substring(10))
            }

            override fun onFinish() {
                viewModel.isTimerStarted = false
                if (isResumed) {
                    onTimerEndsAction()
                } else {
                    isTimerEndsActionDone = false
                }
            }
        }.start()
    }

    private fun onTimerEndsAction() {
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

    companion object {
        private val BUNDLE_CONFIRM_CODE_FRAGMENT_PHONE by lazy { "phone" }
        fun newInstance(phone: String) = ConfirmCodeForgotFragment().apply {
            arguments = Bundle().apply {
                putString(BUNDLE_CONFIRM_CODE_FRAGMENT_PHONE, phone)
            }
        }
    }
}
