package com.amir.ir.privatestore.ui.bindingadapters

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("sizeNameDetermine")
fun extractSizeNameFromId(tv: TextView, sizeId: Int) {
    tv.text = "XL" //todo make it and enum use when
}