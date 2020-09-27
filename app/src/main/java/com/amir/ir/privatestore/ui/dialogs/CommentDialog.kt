package com.amir.ir.privatestore.ui.dialogs

import android.content.Context
import com.amir.ir.privatestore.R
import kotlinx.android.synthetic.main.custom_dialog_send_comment.*

class CommentDialog(
    context: Context,
    themeResId: Int,
    private val onSubmit: (String, Float, CommentDialog) -> Unit
) : MyBaseDialog(
    context, themeResId,
    R.layout.custom_dialog_send_comment
) {
    override fun initViews() {
        btnSendComment.setOnClickListener {
            onSubmit(etComment.text.toString().trim(), rating_bar.rating, this)
        }

        ivClose.setOnClickListener { dismiss() }
    }

    fun showProgress(shouldShow: Boolean) {
        btnSendComment.showProgressBar(shouldShow)
    }
}