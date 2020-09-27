package com.amir.ir.privatestore.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.UserConfigs
import com.amir.ir.privatestore.databinding.ActivityAddAddressBinding
import com.amir.ir.privatestore.models.Address
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.ui.dialogs.NoNetworkDialog
import com.amir.ir.privatestore.utils.*
import com.amir.ir.privatestore.viewmodels.AddAddressActivityViewModel

class AddAddressActivity : AppCompatActivity(), ApiService.InternetConnectionListener,
    ApiService.OnUnauthorizedListener {

    lateinit var viewModel: AddAddressActivityViewModel
    lateinit var mBinding: ActivityAddAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        viewModel = ViewModelProvider(this).get(AddAddressActivityViewModel::class.java)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_address)

        mBinding.viewModel = viewModel
        mBinding.lifecycleOwner = this

        val address: Address? =
            intent.getParcelableExtra(Constants.INTENT_ADD_ADDRESS_ACTIVITY_ADDRESS)
        address?.let {
            viewModel.isActivityOpenedForEdit = true
            viewModel.setAddress(it)
        }

        subscribeObservers()
        initViews()
    }

    private fun subscribeObservers() {
        viewModel.address.observe(this, Observer {
            mBinding.etTitle.setText(if (it.title != "بدون عنوان") it.title else "")
            if (it.ownerName.isNotEmpty())
                mBinding.etName.setText(it.ownerName)
        })

        viewModel.updateAddressEvent.observe(this, EventObserver {
            mBinding.btnSave.showProgressBar(false)
            when (it) {
                "done" -> {
                    mBinding.btnSave.showProgressBar(false)
                    toast("تغییر یافت")
                    finish()
                }
                "" -> {
                    mBinding.btnSave.setOnClickListener(null)
                    snack(Constants.SERVER_ERROR) {
                        saveAddressToViewModel()
                    }
                }
                else -> {
                    toast(it)
                }
            }
        })

        viewModel.insertAddressEvent.observe(this, EventObserver {
            mBinding.btnSave.showProgressBar(false)
            when (it) {
                "done" -> {
                    toast("ذخیره شد")
                    finish()
                }
                "" -> {
                    snack(Constants.SERVER_ERROR) {
                        saveAddressToViewModel()
                    }
                }
                else -> {
                    toast(it)
                }
            }
        })


    }


    private fun saveAddressToViewModel() {
        makeAddressFromInputFields().run {
            when {
                address.isEmpty() || postCode.isEmpty() || ownerName.isEmpty() || !viewModel.isCitySet || !viewModel.isProvinceSet || !viewModel.isLatLngSet -> {
                    if (viewModel.isLatLngSet && province != null && city != null) {
                        toast("لطفا همه موارد را کامل کنید")
                    } else {
                        when {
                            !viewModel.isLatLngSet -> toast("لطفا مکان خود را از روی نقشه مشخص کنید")
                            !viewModel.isProvinceSet -> toast("لطفا استان خود را مشخص کنید")
                            !viewModel.isCitySet -> toast("لطفا شهر خود را مشخص کنید")
                        }
                    }
                    mBinding.btnSave.setOnClickListener { saveAddressToViewModel() }
                }
                postCode.length != 10 -> {
                    toast("کد پستی باید ده رقم باشد")
                }
                EmojiUtils.containsEmoji(this.toString()) -> {
                    toast("ایموجی قابل قبول نمیباشد")
                }
                else -> {
                    mBinding.btnSave.setOnClickListener(null)
                    mBinding.btnSave.showProgressBar(true)
                    viewModel.saveAddressToServerAndDB(this) //send input field to viewModel
                }
            }
        }

    }


    private fun makeAddressFromInputFields(): Address {
        val name = mBinding.etName.text.toString().trim()
        val title = mBinding.etTitle.text.toString().trim()
        val address = mBinding.etAddress.text.toString().trim()
        val pelak = mBinding.etPelak.text.toString().trim()
        val postCode = mBinding.etPostCode.text.toString().trim()
        return Address(
            originalTitle = title,
            ownerName = name,
            address = address,
            pelak = pelak,
            postCode = postCode,
            province = null,
            city = null
        )
    }

    private fun initViews() {
        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }

        /* mBinding.frameMap.setOnClickListener { //dar surate estefade az googleMaps, Alan MapBox Zadim
            if (isGoogleServiceOK()) {
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra(Constants.INTENT_MAP_ACTIVITY_ADDRESS, viewModel.address.value)
                startActivity(intent)
            }
        }*/

        mBinding.frameMap.setOnClickListener { //dar surate estefade az googleMaps, Alan MapBox Zadim
            //   if (isGoogleServiceOK()) {
            viewModel.setAddress(makeAddressFromInputFields())
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra(Constants.INTENT_MAP_ACTIVITY_ADDRESS, viewModel.address.value)
            startActivityForResult(intent, MapsActivity.REQUEST_CODE_MAPS_ACTIVITY)
            // }
        }

        mBinding.btnSave.setOnClickListener { saveAddressToViewModel() }

        UserConfigs.userVal?.let {
            mBinding.etName.setText(it.name)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            MapsActivity.REQUEST_CODE_MAPS_ACTIVITY -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.let {
                        val address =
                            data.getParcelableExtra<Address>(Constants.INTENT_MAP_ACTIVITY_ADDRESS)
                        viewModel.setAddress(address, true)
                    }
                }
            }
        }


    }

    override fun onInternetUnavailable() {
        NoNetworkDialog(this, R.style.my_alert_dialog).show()
    }

    override fun onUnauthorizedAction(event: Event<Unit>) {
        toast(Constants.UNAUTHORIZED_MSG)
        startActivity(Intent(this, LoginActivity::class.java))
    }

    /*private fun isGoogleServiceOK(): Boolean {
        Log.i(TAG, " isServiceOK: checking google service version")
        val instance = GoogleApiAvailability.getInstance()
        val available = instance.isGooglePlayServicesAvailable(this)

        return when {
            available == ConnectionResult.SUCCESS -> true
            instance.isUserResolvableError(available) -> {
                Log.i(TAG, "isServerOK: an error occurred but we can fix it")
                instance.getErrorDialog(this, available, ERROR_DIALOG_REQUESTS).show()
                false
            }
            else -> {
                toast("You can't use map")
                false
            }
        }
    }*/

}
