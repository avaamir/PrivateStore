package com.amir.ir.privatestore.ui.fragments.bottomsheets

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.LayoutChangeProfilePicFragmentBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChangeProfilePicBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var mBinding: LayoutChangeProfilePicFragmentBinding
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
            R.layout.layout_change_profile_pic_fragment,
            container,
            false
        )
        initViews()
        return mBinding.root
    }

    private fun initViews() {
        mBinding.btnCancel.setOnClickListener {
            dismiss()
        }

        mBinding.btnChooseFromGallery.setOnClickListener {
            dismiss()
            interactions.chooseFromGalleryClicked()
        }

        mBinding.btnDeletePic.setOnClickListener {
            dismiss()
            interactions.deleteProfilePicClicked()
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.interactions = activity as Interactions
    }

    interface Interactions {
        fun deleteProfilePicClicked()
        fun chooseFromGalleryClicked()
    }

}