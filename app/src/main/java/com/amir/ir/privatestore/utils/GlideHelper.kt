package com.amir.ir.privatestore.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


@BindingAdapter("profileUrl")
fun loadProfilePic(mProfileImage: ImageView, picUrl: String?) {
    println("debug: glideUrl:$picUrl")
    val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .placeholder(R.drawable.ic_profile_placeholder2)
    Glide.with(mProfileImage.context)
        .setDefaultRequestOptions(requestOptions)
        .load(if (picUrl != null) ApiService.Domain + picUrl else "") //.transition(new DrawableTransitionOptions().crossFade()) //transition baraye circleImageView nemishe
        .into(mProfileImage)
}

@BindingAdapter("imageUrl")
fun loadPic(iv: ImageView, picUrl: String?) {
    //val _picUrl = picUrl?.toInt() //TODO in khat hatman bayad hazf shavad //test purpose
    val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .placeholder(R.drawable.ic_place_holder)
    Glide.with(iv.context)
        .setDefaultRequestOptions(requestOptions)
        .load(ApiService.Domain + (picUrl ?: "")
        ) //.transition(new DrawableTransitionOptions().crossFade()) //transition baraye circleImageView nemishe
        .into(iv)
}

@BindingAdapter("imageUrl")
fun loadPic(mProfileImage: ImageView, resId: Int) {
    val requestOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .placeholder(R.drawable.ic_place_holder)
    Glide.with(mProfileImage.context)
        .setDefaultRequestOptions(requestOptions)
        .load(resId) //.transition(new DrawableTransitionOptions().crossFade()) //transition baraye circleImageView nemishe
        .into(mProfileImage)
}