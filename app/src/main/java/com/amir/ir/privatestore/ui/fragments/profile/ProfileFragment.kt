package com.amir.ir.privatestore.ui.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.LayoutEditProfileFragmentBinding
import com.amir.ir.privatestore.ui.adapters.MessageAdapter
import com.amir.ir.privatestore.ui.fragments.bottomsheets.ChangeNameBottomSheetDialogFragment
import com.amir.ir.privatestore.ui.fragments.bottomsheets.ChangePasswordBottomSheetDialogFragment
import com.amir.ir.privatestore.utils.Constants
import com.amir.ir.privatestore.utils.toast
import com.amir.ir.privatestore.viewmodels.ProfileFragmentViewModel


class ProfileFragment : Fragment() {

    private lateinit var mAdapter: MessageAdapter
    private lateinit var mBinding: LayoutEditProfileFragmentBinding
    private lateinit var viewModel: ProfileFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_edit_profile_fragment,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(ProfileFragmentViewModel::class.java)
        mBinding.viewModel = viewModel
        mBinding.lifecycleOwner = viewLifecycleOwner

        initViews()
        subscribeObservers()

        viewModel.getMessages()

        return mBinding.root
    }

    private fun initViews() {
        mBinding.frameChangeName.setOnClickListener {
            val changeNameBottomSheet = ChangeNameBottomSheetDialogFragment()
            changeNameBottomSheet.show(childFragmentManager, null)
        }

        mBinding.frameChangePassword.setOnClickListener {
            val changePasswordBottomSheet = ChangePasswordBottomSheetDialogFragment()
            changePasswordBottomSheet.show(childFragmentManager, null)
        }


        mBinding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mAdapter = MessageAdapter()
        mBinding.recyclerView.adapter = mAdapter

        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.RIGHT || direction == ItemTouchHelper.LEFT) {
                    viewModel.delete(mAdapter.currentList[viewHolder.position])
                }
            }
        }).attachToRecyclerView(mBinding.recyclerView)

        mBinding.recyclerView.isNestedScrollingEnabled = false

        mBinding.recyclerView.isFocusable = false
        mBinding.frameScrollView.requestFocus()
    }

    private fun subscribeObservers() {
        viewModel.messages.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                mAdapter.submitList(it)
                mBinding.tvMessagesCount.text = it.size.toString()
                if (it.isEmpty()) {
                    mBinding.frameNoMessage.visibility = View.VISIBLE
                    mBinding.recyclerView.visibility = View.GONE
                } else {
                    mBinding.frameNoMessage.visibility = View.GONE
                    mBinding.recyclerView.visibility = View.VISIBLE
                }
            } else {
                mBinding.tvMessagesCount.text = "0"
                mBinding.frameNoMessage.visibility = View.VISIBLE
                mBinding.recyclerView.visibility = View.GONE
                toast(Constants.SERVER_ERROR)
            }
        })
    }

}