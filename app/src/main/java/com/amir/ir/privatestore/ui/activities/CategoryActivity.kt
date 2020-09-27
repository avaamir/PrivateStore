package com.amir.ir.privatestore.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.databinding.ActivityCategoryBinding
import com.amir.ir.privatestore.models.Category
import com.amir.ir.privatestore.models.Slide
import com.amir.ir.privatestore.models.enums.Field
import com.amir.ir.privatestore.models.enums.ListOrder
import com.amir.ir.privatestore.models.enums.SlideType
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.ui.adapters.CategoryAdapter
import com.amir.ir.privatestore.ui.adapters.ProductHorizontalAdapter
import com.amir.ir.privatestore.ui.animations.closeReveal
import com.amir.ir.privatestore.ui.animations.crossfade
import com.amir.ir.privatestore.ui.animations.startReveal
import com.amir.ir.privatestore.ui.dialogs.NoNetworkDialog
import com.amir.ir.privatestore.ui.listeners.CategoryAdapterInteractionImpl
import com.amir.ir.privatestore.ui.listeners.ProductAdapterInteractionSimpleImpl
import com.amir.ir.privatestore.utils.*
import com.amir.ir.privatestore.viewmodels.CategoryActivityViewModel
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.template_category.*
import kotlinx.android.synthetic.main.template_most_sell.*
import kotlinx.android.synthetic.main.template_new_products.*
import kotlinx.android.synthetic.main.template_our_recommend.*
import kotlinx.android.synthetic.main.template_slides_category.*

class CategoryActivity : AppCompatActivity(), ApiService.InternetConnectionListener {


    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var productAdapterOurOffer: ProductHorizontalAdapter
    private lateinit var productAdapterMostSell: ProductHorizontalAdapter
    private lateinit var productAdapterNew: ProductHorizontalAdapter

    private lateinit var viewModel: CategoryActivityViewModel
    private lateinit var mBinding: ActivityCategoryBinding

    private var isSearchShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        viewModel = ViewModelProvider(this).get(CategoryActivityViewModel::class.java)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_category)

        mBinding.viewModel = viewModel
        mBinding.lifecycleOwner = this

        mBinding.frameMain.visibility = View.INVISIBLE
        mBinding.progressMain.visibility = View.VISIBLE

        initViews()
        subscribeObservers()


        val category: Category? =
            intent.getParcelableExtra(Constants.INTENT_CATEGORY_ACTIVITY_CATEGORY)
        val catId: Int = intent.getIntExtra(Constants.INTENT_CATEGORY_ACTIVITY_CAT_ID, 0)
        viewModel.setCategoryOrCatId(category, catId)
    }

    override fun onBackPressed() {
        if (isSearchShown) {
            closeSearchBar()
            isSearchShown = false
        } else {
            super.onBackPressed()
        }
    }

    private fun closeSearchBar() {
        isSearchShown = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            closeReveal(frame_search) {
                frame_search.visibility = View.INVISIBLE
            }
        } else {
            frame_search.visibility = View.INVISIBLE
        }
        frame_toolbar_buttons.visibility = View.VISIBLE
        hideSoftKeyboard()
        etSearch.text.clear()
    }


    private fun subscribeObservers() {
        viewModel.categoryPageResponse.observe(this, Observer { categoryPageResponse ->
            if (categoryPageResponse != null) {
                //recyclers
                if (categoryPageResponse.subCategories.isNotEmpty()) {
                    categoryAdapter.submitList(categoryPageResponse.subCategories)
                } else {
                    mBinding.categoryFrame.visibility = View.GONE
                }
                if (categoryPageResponse.mostSellProducts.isNotEmpty()) {
                    productAdapterMostSell.submitList(categoryPageResponse.mostSellProducts)
                } else {
                    mBinding.frameMostSell.visibility = View.GONE
                }
                if (categoryPageResponse.offeredProducts.isNotEmpty()) {
                    productAdapterOurOffer.submitList(categoryPageResponse.offeredProducts)
                } else {
                    mBinding.frameOurRecommend.visibility = View.GONE
                }
                if (categoryPageResponse.newProducts.isNotEmpty()) {
                    productAdapterNew.submitList(categoryPageResponse.newProducts)
                } else {
                    mBinding.frameNewest.visibility = View.GONE
                }
                //
                if (viewModel.isFirstTime) {
                    crossfade(mBinding.frameMain, mBinding.progressMain)
                } else {
                    mBinding.frameMain.visibility = View.VISIBLE
                    mBinding.progressMain.visibility = View.GONE
                }

                when (categoryPageResponse.slides.size) {
                    0 -> frame_slides.visibility = View.GONE
                    1 -> {
                        frame_slide_2.visibility = View.GONE
                        frame_slide_3.visibility = View.GONE
                    }
                    2 -> {
                        frame_slide_3.visibility = View.GONE
                    }
                    else -> {
                        //todo not implemented in view
                    }
                }

            } else {
                //mBinding.frameMain.visibility = View.INVISIBLE
                mBinding.progressMain.visibility = View.INVISIBLE
                snack(Constants.SERVER_ERROR, {
                    mBinding.progressMain.visibility = View.INVISIBLE
                })
            }
        })
    }

    private fun initViews() {
        ivSearch.setOnClickListener {
            isSearchShown = true
            frame_toolbar_buttons.visibility = View.INVISIBLE
            frame_search.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startReveal(frame_search) {
                    if (etSearch.requestFocus()) {
                        showSoftKeyboard(etSearch)
                    }
                }
            } else {
                if (etSearch.requestFocus()) {
                    showSoftKeyboard(etSearch)
                }
            }
        }

        ivClose.setOnClickListener {  //TODO closeReveal kar nemikonad??
            closeSearchBar()
        }

        mBinding.etSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchKey = etSearch.text.toString().trim()
                when {
                    searchKey.isBlank() -> {
                        toast(getString(R.string.error_search_empty))
                    }
                    searchKey.length < 3 -> {
                        toast(getString(R.string.error_search_length))
                    }
                    else -> {
                        val intent = Intent(this, ProductListActivity::class.java)
                        intent.putExtra(
                            Constants.INTENT_PRODUCT_LIST_ACTIVITY_SEARCH_KEY,
                            searchKey
                        )
                        intent.putParcelableExtra(
                            Constants.INTENT_PRODUCT_LIST_ACTIVITY_FIELD,
                            Field.CATEGORY
                        ) //vase search be field niaz nist ama chun logic getProduct va Search ro ba ham edgham kardam majburam befrestam -> mahal estefade: taeed toolbar title va estefade az id category dar search
                        intent.putParcelableExtra(
                            Constants.INTENT_PRODUCT_LIST_ACTIVITY_CAT,
                            viewModel.category.copy(title = "جست و جو در ${viewModel.category.title}")
                        )
                        startActivity(intent)
                    }
                }
            }
            false
        }


        //recyclers
        val productAdapterInteractions = ProductAdapterInteractionSimpleImpl(this)

        recyclerCategory.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        categoryAdapter = CategoryAdapter(CategoryAdapterInteractionImpl(this))
        recyclerCategory.adapter = categoryAdapter

        recyclerOurRecommend.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        productAdapterOurOffer = ProductHorizontalAdapter(productAdapterInteractions)
        recyclerOurRecommend.adapter = productAdapterOurOffer

        recyclerMostSell.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        productAdapterMostSell = ProductHorizontalAdapter(productAdapterInteractions)
        recyclerMostSell.adapter = productAdapterMostSell

        recyclerNewProducts.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        productAdapterNew = ProductHorizontalAdapter(productAdapterInteractions)
        recyclerNewProducts.adapter = productAdapterNew

        //Buttons
        ivBack.setOnClickListener {
            //todo cancell kardan job haye marbut be in
            //todo viewModel.cancelJobs()
            super.onBackPressed()
        }

        ivCart.setOnClickListener {
            startActivity(Intent(this@CategoryActivity, CartActivity::class.java))
        }

        tvFullMostSellProducts.setOnClickListener {
            val intent = Intent(this@CategoryActivity, ProductListActivity::class.java)
            intent.putParcelableExtra(Constants.INTENT_PRODUCT_LIST_ACTIVITY_FIELD, Field.CATEGORY)
            intent.putParcelableExtra(
                Constants.INTENT_PRODUCT_LIST_ACTIVITY_CAT,
                viewModel.category
            )
            intent.putParcelableExtra(
                Constants.INTENT_PRODUCT_LIST_ACTIVITY_ORDER_ID,
                ListOrder.MOST_SELL
            )
            startActivity(intent)
        }

        tvFullNewProducts.setOnClickListener {
            val intent = Intent(this@CategoryActivity, ProductListActivity::class.java)
            intent.putParcelableExtra(Constants.INTENT_PRODUCT_LIST_ACTIVITY_FIELD, Field.CATEGORY)
            intent.putParcelableExtra(
                Constants.INTENT_PRODUCT_LIST_ACTIVITY_CAT,
                viewModel.category
            )
            intent.putParcelableExtra(
                Constants.INTENT_PRODUCT_LIST_ACTIVITY_ORDER_ID,
                ListOrder.NEW
            )
            startActivity(intent)
        }

        tvFullRecommends.setOnClickListener {
            val intent = Intent(this@CategoryActivity, ProductListActivity::class.java)
            intent.putParcelableExtra(
                Constants.INTENT_PRODUCT_LIST_ACTIVITY_FIELD,
                Field.OUR_RECOMMEND
            ) //todo vaghti field category hsat bayad be catID ham tavajoh shavad
            intent.putParcelableExtra(
                Constants.INTENT_PRODUCT_LIST_ACTIVITY_CAT,
                viewModel.category
            )
            startActivity(intent)
        }

        //slides
        val onSlideClickedListenerImpl = View.OnClickListener { view ->
            val categoryPageResponse = viewModel.categoryPageResponse.value
            categoryPageResponse?.let {
                when (view) {
                    frame_slide_1 -> onSlideClicked(it.slides[0])
                    frame_slide_2 -> onSlideClicked(it.slides[1])
                    frame_slide_3 -> onSlideClicked(it.slides[2])
                }
            }
        }
        frame_slide_1.setOnClickListener(onSlideClickedListenerImpl)
        frame_slide_2.setOnClickListener(onSlideClickedListenerImpl)
        frame_slide_3.setOnClickListener(onSlideClickedListenerImpl)
    }


    private fun onSlideClicked(slide: Slide) {
        when (slide.type) {
            SlideType.NOT_DEFINED -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(slide.link)
                startActivity(intent)
                println("debug: SliderInteractionImpl: NOT_Defined Slide Type -> update app")
            }
            SlideType.PRODUCT -> {
                val intent = Intent(this, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.INTENT_PRODUCT_DETAILS_ACTIVITY_PID, slide.targetId)
                startActivity(intent)
            }
            SlideType.CATEGORY -> {
                val intent = Intent(this, CategoryActivity::class.java)
                intent.putExtra(Constants.INTENT_CATEGORY_ACTIVITY_CATEGORY, slide.targetId)
                startActivity(intent)
            }
        }.exhaustive()
    }

    override fun onInternetUnavailable() {
        NoNetworkDialog(this, R.style.my_alert_dialog).show()
    }

}
