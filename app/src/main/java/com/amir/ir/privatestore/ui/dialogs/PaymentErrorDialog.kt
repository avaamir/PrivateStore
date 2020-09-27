package com.amir.ir.privatestore.ui.dialogs

import android.content.Context
import com.amir.ir.privatestore.R
import kotlinx.android.synthetic.main.layout_discount_error_continue_payment_dialog.*

class PaymentErrorDialog(
    context: Context,
    themeResId: Int,
    private val message: String,
    private val onContinuePayment: (Boolean, PaymentErrorDialog) -> Unit
) : MyBaseDialog(
    context, themeResId,
    R.layout.layout_discount_error_continue_payment_dialog
) {
    override fun initViews() {
        tvMessage.text = message
        btn_accept.setOnClickListener {
            onContinuePayment(true, this)
        }
        btn_denied.setOnClickListener {
            onContinuePayment(false, this)
        }
    }
}