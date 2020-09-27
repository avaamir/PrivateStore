package com.amir.ir.privatestore.ui.bindingadapters

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.models.Color


private fun extractColor(name: String?): Int {
    return if (name == null)
        android.R.color.transparent
    else when (name) {
        "بدون رنگ" -> android.R.color.transparent
        "قرمز" -> R.color.deep_red
        "آبی" -> R.color.blue
        "بنفش" -> R.color.purple
        "نارنجی" -> R.color.deep_orange
        "زرد" -> R.color.yellow
        "سبز" -> R.color.green
        "صورتی" -> R.color.pink
        "مشکی" -> R.color.black
        else -> {
            Log.d(
                "debug: ",
                "StaticValues.extractColor():: Error: case default: $name"
            )
            android.R.color.transparent
        }
    }
}

/*
@BindingAdapter("pickColorDetermine")
fun setColor(ivColor: ImageView, colorName: String) {
    ivColor.setImageResource(extractColor(colorName))
}*/

@BindingAdapter("pickColorDetermine")
fun setColor(ivColor: ImageView, color: Color?) {
    color?.let {
        try {
            ivColor.background = ShapeDrawable(OvalShape()).apply {
                paint.color = android.graphics.Color.parseColor(color.colorCode)
                intrinsicHeight = 50
                intrinsicWidth = 50
            }
        } catch (ex: Exception) {
            ivColor.setImageResource(extractColor(color.name))
        }
    }
}
