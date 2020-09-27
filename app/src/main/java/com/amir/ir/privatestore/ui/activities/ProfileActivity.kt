package com.amir.ir.privatestore.ui.activities

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.UserConfigs
import com.amir.ir.privatestore.databinding.ActivityProfileBinding
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.ui.adapters.FragmentViewPagerAdapter
import com.amir.ir.privatestore.ui.animations.closeReveal
import com.amir.ir.privatestore.ui.animations.startReveal
import com.amir.ir.privatestore.ui.dialogs.NoNetworkDialog
import com.amir.ir.privatestore.ui.fragments.bottomsheets.ChangeNameBottomSheetDialogFragment
import com.amir.ir.privatestore.ui.fragments.bottomsheets.ChangePasswordBottomSheetDialogFragment
import com.amir.ir.privatestore.ui.fragments.bottomsheets.ChangeProfilePicBottomSheetDialogFragment
import com.amir.ir.privatestore.ui.fragments.profile.AddressFragment
import com.amir.ir.privatestore.ui.fragments.profile.FavoriteFragment
import com.amir.ir.privatestore.ui.fragments.profile.ProfileFragment
import com.amir.ir.privatestore.ui.fragments.profile.TrackOrdersFragment
import com.amir.ir.privatestore.utils.*
import com.amir.ir.privatestore.viewmodels.ProfileActivityViewModel
import com.google.android.material.appbar.AppBarLayout
import java.io.File

class ProfileActivity : AppCompatActivity(), ApiService.OnUnauthorizedListener,
    ImageRequestPreparationHelper.Interactions, ChangeNameBottomSheetDialogFragment.Interactions,
    ChangePasswordBottomSheetDialogFragment.Interactions,
    ChangeProfilePicBottomSheetDialogFragment.Interactions, ApiService.InternetConnectionListener {

    private var shouldEndAnim = false

    //private var scrollRange = 0f
    //private var viewPagerPosition = 4
    //private var isRevealEndStart = false

    private var changePasswordDialog: ChangePasswordBottomSheetDialogFragment? = null


    private lateinit var mBinding: ActivityProfileBinding
    private lateinit var viewModel: ProfileActivityViewModel

    private lateinit var helper: ImageRequestPreparationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        viewModel = ViewModelProvider(this).get(ProfileActivityViewModel::class.java)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = viewModel

        helper = ImageRequestPreparationHelper(this, this)

        initViews()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.deleteProfilePicResponse.observe(this, Observer {
            stopProgressAnimation()
            if (it != null) {
                if (it.error) {
                    toast(it.errorMsg ?: Constants.SERVER_ERROR)
                } else {
                    toast("عکس پروفایل شما حذف شد")
                    //mBinding.ivProfile.setImageResource(R.drawable.ic_profile_placeholder2)
                }
            } else {
                toast(Constants.SERVER_ERROR)
            }
        })

        viewModel.changeProfileResponse.observe(this, Observer {
            stopProgressAnimation()
            changePasswordDialog?.shouldShowProgress(false)
            if (it != null) {
                if (it.error) {
                    toast(it.errorMsg ?: Constants.SERVER_ERROR)
                } else {
                    toast("پروفایل شما تغییر یافت")
                    changePasswordDialog?.let { dialog ->
                        dialog.dismiss()
                        changePasswordDialog = null
                    }
                }
            } else {
                toast(Constants.SERVER_ERROR)
            }
        })
    }

    private fun initViews() {
        mBinding.btnAddAddress.setOnClickListener {
            startActivity(Intent(this, AddAddressActivity::class.java))
        }

        mBinding.btnChangeProfilePic.setOnClickListener {
            val changeProfilePicBottomSheet = ChangeProfilePicBottomSheetDialogFragment()
            changeProfilePicBottomSheet.show(supportFragmentManager, null)
        }

        mBinding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, offset ->
            appBarLayout?.run {
                val scrollRange = (totalScrollRange + 2 * offset).toFloat() / totalScrollRange
                mBinding.frameProfile.animate().scaleX(scrollRange).scaleY(scrollRange)
                    .setDuration(0).start()

                /*if (viewPagerPosition == 0) {
                    if (scrollRange < 0.9f) {
                        if (mBinding.btnChangeProfilePic.visibility == View.VISIBLE && !isRevealEndStart)
                            isRevealEndStart = true
                            eReveal(mBinding.btnChangeProfilePic)
                    } else {
                        if (mBinding.btnChangeProfilePic.visibility != View.VISIBLE) {
                            isRevealEndStart = false
                            sReveal(mBinding.btnChangeProfilePic)
                        }
                    }
                }*/
            }
        })

        mBinding.viewPager.adapter = FragmentViewPagerAdapter(
            supportFragmentManager,
            initFragments(),
            initTitles()
        )

        mBinding.viewPager.offscreenPageLimit = 4

        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager)

        val page = intent.getIntExtra(Constants.INTENT_PROFILE_ACTIVITY_PAGE, mBinding.tabLayout.tabCount - 1)
        mBinding.viewPager.currentItem = page


        mBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }


            override fun onPageSelected(position: Int) {
                //viewPagerPosition = position
                if (position == 0) {
                    sReveal(mBinding.btnChangeProfilePic)
                } else {
                    eReveal(mBinding.btnChangeProfilePic)
                }

                if (position == 2) {
                    sReveal(mBinding.btnAddAddress)
                } else {
                    eReveal(mBinding.btnAddAddress)
                }
            }

        })
    }

    private fun initTitles(): ArrayList<String> {
        val titles = ArrayList<String>()
        titles.add("مشخصات")
        titles.add("سفارشات شما")
        titles.add("آدرس ها")
        titles.add("نشان شده ها")
        return titles
    }

    private fun initFragments(): Array<Fragment> {
        return arrayOf(
            ProfileFragment(),
            TrackOrdersFragment(),
            AddressFragment(),
            FavoriteFragment()
        )
    }


    fun sReveal(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.visibility = View.VISIBLE
            startReveal(view) {}
        } else {
            view.apply {
                scaleX = 0f
                scaleY = 0f
                visibility = View.VISIBLE
            }.animate().setDuration(200).scaleX(1f).scaleY(1f).start()
        }
    }

    fun eReveal(view: View) {
        if (view.visibility == View.VISIBLE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                closeReveal(view) {
                    view.visibility = View.GONE
                }
            } else {
                val anim = view.animate()
                anim.setDuration(200)
                    .scaleX(0f)
                    .scaleY(0f)
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {}
                        override fun onAnimationCancel(animation: Animator?) {}
                        override fun onAnimationStart(animation: Animator?) {}
                        override fun onAnimationEnd(animation: Animator?) {
                            view.visibility = View.GONE
                            anim.setListener(null)
                        }
                    }).start()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        helper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        helper.onActivityResult(requestCode, resultCode, data)
    }

    override fun onUnauthorizedAction(event: Event<Unit>) {
        event.getEventIfNotHandled()?.let {
            toast(Constants.UNAUTHORIZED_MSG, true)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onImageNotChosen() {
        toast("انتخاب عکس الزامی میباشد")
    }

    override fun onImageNotFound() {
        toast("فایل مورد نظر پیدا نشد")
    }

    override fun beforeRequestPermissionDialogMessage(permissionRequesterFunction: () -> Unit) {
        //TODO show a dialog
        permissionRequesterFunction.invoke()
    }

    override fun onPermissionDenied() {
        toast("اجازه دسترسی داده نشد!")
    }

    override fun onImageReady(compressedFile: File, compressedBitmap: Bitmap) {
        //mBinding.ivProfile.setImageBitmap(compressedBitmap)
        viewModel.changeProfile(compressedFile)
        startProgressAnimation()
    }

    override fun onChangeNameSaveClicked(name: String) {
        viewModel.changeProfile(name = name)
    }

    override fun onChangePasswordClicked(
        changePasswordDialog: ChangePasswordBottomSheetDialogFragment,
        oldPassword: String,
        newPassword: String
    ) {
        changePasswordDialog.shouldShowProgress(true)
        this.changePasswordDialog = changePasswordDialog
        viewModel.changeProfile(oldPassword = oldPassword, newPassword = newPassword)
    }

    override fun onChangePasswordCanceled() {
        changePasswordDialog = null
    }

    override fun deleteProfilePicClicked() {
        if (UserConfigs.userVal?.profilePic != null) {
            alert(
                "تصویر پروفایل",
                "تصویر پروفایل شما حذف خواهد شد. ادامه میدهید؟",
                "بله، حذف شود",
                "خیر",
                null
            ) {
                viewModel.deleteProfilePic()
                startProgressAnimation()
            }
        }
    }

    private val animator by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            duration = 1000
        }
    }

    private fun startProgressAnimation() {
        shouldEndAnim = false
        mBinding.ivProfile.borderWidth = 4
        val argbEvaluator = ArgbEvaluator()
        val colorPrimary = ContextCompat.getColor(this, R.color.primary)
        animator.addUpdateListener { animation ->
            val colorOffset = argbEvaluator.evaluate(
                animation.animatedValue as Float,
                Color.WHITE, colorPrimary
            ) as Int
            mBinding.ivProfile.borderColor = colorOffset
            if (shouldEndAnim && colorOffset == Color.WHITE) {
                animator.cancel()
                mBinding.ivProfile.borderWidth = 2
            }
        }
        animator.start()
    }

    private fun stopProgressAnimation() {
        shouldEndAnim = true
    }

    override fun chooseFromGalleryClicked() {
        helper.checkPermissionAndPickImage()
    }

    override fun onInternetUnavailable() {
        NoNetworkDialog(this, R.style.my_alert_dialog).show()
    }
}
