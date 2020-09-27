package com.amir.ir.privatestore.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.amir.ir.privatestore.UserConfigs
import com.amir.ir.privatestore.models.User
import com.amir.ir.privatestore.models.requests.EditProfileResponse
import com.amir.ir.privatestore.repository.RemoteRepo
import com.amir.ir.privatestore.utils.Event
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfileActivityViewModel : ViewModel() {

    private val deleteProfilePicEvent = MutableLiveData<Event<Any>>()
    val deleteProfilePicResponse = Transformations.switchMap(deleteProfilePicEvent) {
        RemoteRepo.deleteProfilePic()
    }

    private val _changeProfileResponse = MutableLiveData<EditProfileResponse?>()
    val changeProfileResponse: LiveData<EditProfileResponse?> get() = _changeProfileResponse

    val user: LiveData<User?> get() = UserConfigs.user


    fun deleteProfilePic() {
        deleteProfilePicEvent.value = Event(Unit)
    }

    fun changeProfile(
        imageFile: File? = null,
        name: String? = null,
        oldPassword: String? = "lll",
        newPassword: String? = null
    ) {
        val nameRequest =
            RequestBody.create(MediaType.parse("text/plane"), (name ?: UserConfigs.userVal!!.name))

        val oldPasswordRequest = oldPassword?.let {
            RequestBody.create(MediaType.parse("text/plane"), oldPassword)
        }
        val newPasswordRequest = newPassword?.let {
            RequestBody.create(MediaType.parse("text/plane"), newPassword)
        }
        val imageRequest = imageFile?.let {
            //val imgReqBody = imageFile.asRequestBody("*/*".toMediaTypeOrNull())
            //MultipartBody.Part.createFormData("image", imageFile.name, imgReqBody)
            val imgReqBody = RequestBody.create(MediaType.parse("*/*"), imageFile)
            MultipartBody.Part.createFormData("image", imageFile.name, imgReqBody)
        }
        RemoteRepo.changeProfile(nameRequest, oldPasswordRequest, newPasswordRequest, imageRequest) {
            _changeProfileResponse.value = it
        }
    }
}