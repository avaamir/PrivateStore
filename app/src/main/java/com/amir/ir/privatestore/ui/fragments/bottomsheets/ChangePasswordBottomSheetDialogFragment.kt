package com.amir.ir.privatestore.ui.fragments.bottomsheets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.LayoutBottomSheetChangePasswordBinding
import com.amir.ir.privatestore.utils.toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChangePasswordBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var mBinding: LayoutBottomSheetChangePasswordBinding
    private lateinit var interactions: Interactions

    // this is an open issue on github we should use it to prevent navigation bar became black! fuck android
    override fun getTheme(): Int = R.style.CustomBottomSheetDialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)
    //

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_bottom_sheet_change_password,
            container,
            false
        )
        initViews()
        return mBinding.root
    }

    private fun initViews() {
        mBinding.btnCancel.setOnClickListener {
            interactions.onChangePasswordCanceled()
            dismiss()
        }

        mBinding.btnSave.setOnClickListener {
            val newPassword = mBinding.etNewPassword.text.toString().trim()
            val newPasswordConfirm = mBinding.etNewPasswordConfirm.text.toString().trim()
            val oldPassword = mBinding.etOldPassword.text.toString().trim()
            if (newPasswordConfirm != newPassword) {
                toast("پسورد و تکرار آن یکسان نمیباشد")
            } else {
                interactions.onChangePasswordClicked(this, oldPassword, newPassword)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.interactions = activity as Interactions
    }

    fun shouldShowProgress(shouldShow: Boolean) {
        mBinding.btnSave.showProgressBar(shouldShow)
    }

    interface Interactions {
        fun onChangePasswordClicked(changePasswordDialog: ChangePasswordBottomSheetDialogFragment, oldPassword: String, newPassword: String)
        fun onChangePasswordCanceled()
    }

}