package com.amir.ir.privatestore.ui.activities

import android.content.Intent
import android.graphics.Color.GRAY
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.UserConfigs
import com.amir.ir.privatestore.databinding.ActivityProductDetailsBinding
import com.amir.ir.privatestore.models.Color
import com.amir.ir.privatestore.models.Product
import com.amir.ir.privatestore.models.Size
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.ui.adapters.ImageSliderAdapter
import com.amir.ir.privatestore.ui.adapters.PickColorAdapter
import com.amir.ir.privatestore.ui.adapters.PickSizeAdapter
import com.amir.ir.privatestore.ui.adapters.ProductHorizontalAdapter
import com.amir.ir.privatestore.ui.animations.crossfade
import com.amir.ir.privatestore.ui.bindingadapters.priceToStr
import com.amir.ir.privatestore.ui.dialogs.NoNetworkDialog
import com.amir.ir.privatestore.ui.listeners.ProductAdapterInteractionSimpleImpl
import com.amir.ir.privatestore.utils.*
import com.amir.ir.privatestore.viewmodels.ProductDetailsActivityViewModel
import com.google.android.material.appbar.AppBarLayout
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.android.synthetic.main.body_product_details.*


class ProductDetailsActivity : AppCompatActivity(), PickColorAdapter.OnColorPick,
    AppBarLayout.OnOffsetChangedListener, PickSizeAdapter.OnSizePick,
    ApiService.OnUnauthorizedListener, ImageSliderAdapter.SliderInteractions, ApiService.InternetConnectionListener {


    private lateinit var mAdapter: ProductHorizontalAdapter
    private lateinit var mBinding: ActivityProductDetailsBinding
    private lateinit var viewModel: ProductDetailsActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        //AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        val product: Product? =
            intent.getParcelableExtra(Constants.INTENT_PRODUCT_DETAILS_ACTIVITY_PRODUCT)  //if product == null, az Slide navigate shode be inja va bayad request bezanam
        val pid: Int = intent.getIntExtra(Constants.INTENT_PRODUCT_DETAILS_ACTIVITY_PID, 0)


        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_details)
        viewModel = ViewModelProvider(this).get(ProductDetailsActivityViewModel::class.java)

        mBinding.mainFrame.visibility = View.INVISIBLE


        initViews()
        subscribeObservers()

        if (product == null) {
            viewModel.setPid(pid)
        } else {
            viewModel.setProduct(product)
        }

        mBinding.lifecycleOwner = this
        mBinding.viewModel = viewModel
    }

    override fun onBackPressed() {
        viewModel.cancelJobs()
        super.onBackPressed()
    }

    private fun initViews() {
        btnAddToCart.setOnClickListener {
            viewModel.addToCart()
        }

        btnComments.setOnClickListener {
            val intent = Intent(this, CommentActivity::class.java)
            intent.putExtra(Constants.INTENT_COMMENT_ACTIVITY_PRODUCT, viewModel.product.value!!)
            startActivity(intent)
        }


        btnShare.setOnClickListener {
            shareProduct(viewModel.product.value!!)
        }

        btnTag.setOnClickListener {
            // btnTag.showProgressBar(true) todo age badan be server req zadim ezae beshe, faghat rangesh sefide va background ham sefide che konim??
            if (UserConfigs.isLoggedIn) {
                btnTag.showProgressBar(true)
                viewModel.toggleTag()
            } else {
                viewModel.isRequestLoginForTag = true
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }



        mBinding.ivBack.setOnClickListener {
            super.onBackPressed()
        }

        mBinding.ivCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        recyclerColorPick.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        (recyclerColorPick.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recyclerColorPick.isNestedScrollingEnabled = false

        recyclerSizePick.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        (recyclerSizePick.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        ViewCompat.setNestedScrollingEnabled(
            recyclerSizePick,
            false
        ) //todo check this code. use this or next line?
        //recyclerSizePick.isNestedScrollingEnabled = false //todo check next line code

        mAdapter = ProductHorizontalAdapter(ProductAdapterInteractionSimpleImpl(this))
        recyclerRelatedProducts.adapter = mAdapter
        recyclerRelatedProducts.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        recyclerRelatedProducts.isNestedScrollingEnabled = false

        mBinding.appBar.addOnOffsetChangedListener(this)

        recyclerRelatedProducts.isFocusable = false
        recyclerColorPick.isFocusable = false
        recyclerSizePick.isFocusable = false
        tv_product_name.requestFocus()

    }

    private fun initSlider(images: List<String>) {
        val sliderAdapter = ImageSliderAdapter(images)
        sliderAdapter.setSlideInteractions(this)
        mBinding.sliderView.setSliderAdapter(sliderAdapter)
        //
        mBinding.sliderView.startAutoCycle()
        mBinding.sliderView.setIndicatorAnimation(IndicatorAnimations.WORM)
        mBinding.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        mBinding.sliderView.indicatorSelectedColor = ContextCompat.getColor(this, R.color.primary_light)
        mBinding.sliderView.indicatorUnselectedColor = GRAY
        mBinding.sliderView.scrollTimeInSec = 10 //set scroll delay in seconds :
    }

    private fun subscribeObservers() {

        viewModel.favoriteInsertUpdateResponse.observe(this, Observer {
            if (it != null) {
                if (it.error) {
                    toast(it.errorMsg ?: Constants.SERVER_ERROR)
                }
            } else {
                if (!viewModel.isRequestLoginForTag)
                    toast(Constants.SERVER_ERROR)
            }
        })

        viewModel.favoriteDeleteResponse.observe(this, Observer {
            if (it != null) {
                if (it.error) {
                    toast(it.errorMsg ?: Constants.SERVER_ERROR)
                }
            } else {
                if (!viewModel.isRequestLoginForTag)
                    toast(Constants.SERVER_ERROR)
            }
        })

        viewModel.loggedInEvent.observe(this, EventObserver { isLoggedIn ->
            if (isLoggedIn && viewModel.isRequestLoginForTag) {
                btnTag.showProgressBar(true)
                viewModel.isRequestLoginForTag = false
                viewModel.toggleTag()
            }
        })

        viewModel.isTagged.observe(this, Observer {
            println("debug: scope -> isTagged observer called")
            setBtnTagImage(it)
            btnTag.showProgressBar(false)
        })

        viewModel.navigateToCartEvent.observe(this, EventObserver { shouldNavigate ->
            if (shouldNavigate) {
                val intent = Intent(this, CartActivity::class.java)
                intent.putExtra(Constants.INTENT_CART_ACTIVITY_PRODUCT, viewModel.product.value)
                startActivity(intent)
            } else {
                toast("سبد خرید شما حداکثر تعداد این محصول را دارد")
            }
        })

        viewModel.product.observe(this, Observer { product ->
            if (product != null) {

                if(product.isOutOfStock) {
                    frameExistInStock.visibility = View.GONE
                    tvOutOfStock.visibility = View.VISIBLE
                }

                initSlider(product.images!!)

                if (viewModel.isFirstTime) {
                    crossfade(mBinding.mainFrame, mBinding.progressMain)
                } else {
                    mBinding.mainFrame.visibility = View.VISIBLE
                    mBinding.progressMain.visibility = View.INVISIBLE
                }

                if (!product.hasSize) { //set price from here if product had size
                    if (product.hasDiscount) {
                        gpDiscountPrice.visibility = View.VISIBLE
                        tvPriceGreen.text = priceToStr(product.discountPrice)
                        tvPriceRed.text = priceToStr(product.mainPrice)
                    } else {
                        tvPriceGreen.text = priceToStr(product.mainPrice)
                        gpDiscountPrice.visibility = View.INVISIBLE
                    }
                }

                if (!product.colors.isNullOrEmpty()) {
                    gpColors.visibility = View.VISIBLE
                    recyclerColorPick.adapter = PickColorAdapter(product.colors, this)
                    tvColorCount.text =
                        resources.getString(R.string.color_count, product.colors.size)
                    // viewModel.chosenSize()
                } else {
                    gpColors.visibility = View.GONE
                }

                if (!product.sizes.isNullOrEmpty()) {
                    gpSize.visibility = View.VISIBLE
                    recyclerSizePick.adapter = PickSizeAdapter(product.sizes, this)
                    tvSizeCount.text = resources.getString(R.string.size_count, product.sizes.size)
                    //todo set chosen size from viewModel if exist (for rotate situation)
                } else {
                    gpSize.visibility = View.GONE
                }
            } else {
                mBinding.progressMain.visibility = View.GONE
                snack(Constants.SERVER_ERROR, {
                    mBinding.progressMain.visibility = View.VISIBLE
                    viewModel.tryAgain()
                })
            }
        })

        viewModel.relatedProducts.observe(this, Observer {
            mAdapter.submitList(it)
        })
    }

    private fun setBtnTagImage(activate: Boolean) {
        // btnTag.showProgressBar(false) todo age badan be server req zadim ezae beshe, faghat rangesh sefide va background ham sefide che konim??
        if (activate) {
            btnTag.setImage(R.drawable.ic_favorite_red)
        } else {
            btnTag.setImage(R.drawable.ic_favorite_gray)
        }
    }


    private fun shareProduct(product: Product) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name_fa)


        val productLink = "${ApiService.Domain}/product/${product.slug ?: ""}"
        val shareBody = "${product.name}:\r\n$productLink"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(sharingIntent, "انتخاب کنید"))
    }


    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        /*appBarLayout?.run {
            mBinding.txTest.setTextColor(
                argbEvaluator.evaluate(
                    verticalOffset / appBarLayout.totalScrollRange.toFloat(),
                    0,
                    0xffffff
                ) as Int
                //, android.graphics.PorterDuff.Mode.SRC_IN
            )
        }*/
    }

    override fun onPickColor(color: Color) {
        viewModel.chosenColor = color
    }

    override fun onPickSize(size: Size) {
        viewModel.chosenSize = size
        if (size.hasDiscount) {
            gpDiscountPrice.visibility = View.VISIBLE
            tvPriceGreen.text = priceToStr(size.discountPrice)
            tvPriceRed.text = priceToStr(size.mainPrice)
        } else {
            tvPriceGreen.text = priceToStr(size.mainPrice)
            gpDiscountPrice.visibility = View.INVISIBLE
        }
    }

    override fun onUnauthorizedAction(event: Event<Unit>) {
        toast("حساب کاربری شما در دستگاه دیگری فعال است. لطفا دوباره وارد شوید", true)
        viewModel.isRequestLoginForTag = true
        btnTag.showProgressBar(false)
        startActivity(Intent(this, LoginActivity::class.java))
        //TODO("btn tag ro ke zad bayad check beshe")
    }


    override fun onItemClicked(image: String) {
        //todo ru ax mahsul click kard tori beshe??
    }

    override fun onInternetUnavailable() {
        NoNetworkDialog(this, R.style.my_alert_dialog).show()
    }
}
