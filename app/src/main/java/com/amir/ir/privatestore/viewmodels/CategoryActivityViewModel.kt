package com.amir.ir.privatestore.viewmodels

import androidx.lifecycle.*
import com.amir.ir.privatestore.models.Category
import com.amir.ir.privatestore.models.reponses.CategoryPageResponse
import com.amir.ir.privatestore.repository.RemoteRepo
import com.amir.ir.privatestore.utils.DoubleTrigger

class CategoryActivityViewModel : ViewModel() {

    var isFirstTime = true  //use for crossFade animation
        private set
        get() {
            if(field) {
                field = false
                return true
            }
            return field
        }

    private val catId = MutableLiveData<Int>()

    private var _category: MutableLiveData<Category> = MutableLiveData()

    private val _toolbarTitle: MediatorLiveData<String> =
        MediatorLiveData() //mishod az DoubleTrigger estefade kard vali hal kardam ino bezaram :)
    val toolbarTitle: LiveData<String> get() = _toolbarTitle

    val category
        get() = _category.value ?: Category(
            id = categoryPageResponse.value!!.id,
            title = categoryPageResponse.value!!.title,
            image = ""
        )

    val categoryPageResponse: LiveData<CategoryPageResponse?> =
        Transformations.switchMap(DoubleTrigger(_category, catId)) {
            val category = it.first
            val catId = it.second
            if (category == null) {
                RemoteRepo.getCategoryPage(catId!!)
            } else {
                RemoteRepo.getCategoryPage(category.id)
            }
        }


    init {
        _toolbarTitle.addSource(_category) {
            _toolbarTitle.value = it.title
        }
        _toolbarTitle.addSource(categoryPageResponse) {
            _toolbarTitle.value = it?.title
        }
    }

    fun setCategoryOrCatId(
        category: Category?,
        catId: Int
    ) { //age ez slide hedayat shode bashe faghat catId darim
        if (category == null) {
            if (catId == 0)
                throw (Throwable("catId=0 and category = null is not valid state"))
            if (catId != this.catId.value)
                this.catId.value = catId
        } else {
            if (this._category.value != category)
                this._category.value = category
        }
    }

    fun cancelJobs() {
        RemoteRepo.cancelServerJobs() //todo spicify CategoryPageReponse job, just cancel this
    }


}