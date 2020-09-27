package com.amir.ir.privatestore.ui.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amir.ir.privatestore.BuildConfig
import com.amir.ir.privatestore.MyStoreApplication
import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.TestActivity
import com.amir.ir.privatestore.models.Category
import com.amir.ir.privatestore.models.Slide
import com.amir.ir.privatestore.models.User
import com.amir.ir.privatestore.models.enums.Field
import com.amir.ir.privatestore.repository.apiservice.ApiService
import com.amir.ir.privatestore.ui.adapters.CategoryAdapter
import com.amir.ir.privatestore.ui.adapters.ProductHorizontalAdapter
import com.amir.ir.privatestore.ui.adapters.SliderAdapter
import com.amir.ir.privatestore.ui.animations.closeReveal
import com.amir.ir.privatestore.ui.animations.crossfade
import com.amir.ir.privatestore.ui.animations.startActivityWithTransition
import com.amir.ir.privatestore.ui.animations.startReveal
import com.amir.ir.privatestore.ui.dialogs.NoNetworkDialog
import com.amir.ir.privatestore.ui.dialogs.UpdateAppDialog
import com.amir.ir.privatestore.ui.listeners.CategoryAdapterInteractionImpl
import com.amir.ir.privatestore.ui.listeners.ProductAdapterInteractionSimpleImpl
import com.amir.ir.privatestore.ui.listeners.SliderInteractionImpl
import com.amir.ir.privatestore.utils.*
import com.amir.ir.privatestore.viewmodels.MainActivityViewModel
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.holder.BadgeStyle
import com.mikepenz.materialdrawer.holder.StringHolder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.smarteist.autoimageslider.IndicatorAnimations
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.template_amazing_offers.*
import kotlinx.android.synthetic.main.template_category.*
import kotlinx.android.synthetic.main.template_most_sell.*
import kotlinx.android.synthetic.main.template_new_products.*
import java.util.*

class MainActivity : AppCompatActivity(), ApiService.InternetConnectionListener {

    private var isAmazingTimerEnds = false

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var productAdapterAmazing: ProductHorizontalAdapter
    private lateinit var productAdapterNew: ProductHorizontalAdapter
    private lateinit var productAdapterMostSell: ProductHorizontalAdapter

    private lateinit var viewModel: MainActivityViewModel
    private var isSearchShown = false

    private var isCartActivityOpenedBefore = false

    // drawer
    private lateinit var mDrawer: Drawer
    private lateinit var tvNameDrawer: TextView
    private lateinit var ivProfile: ImageView
    private val drawerHome by lazy {
        PrimaryDrawerItem()
            .withName("خانه")
            .withIcon(R.drawable.ic_homepage_)
            .withTypeface(iransansLight)
            .withTextColor(ContextCompat.getColor(this, R.color.gray900))
            .withOnDrawerItemClickListener { _, _, _ ->
                mDrawer.closeDrawer()
                true
            }
    }
    private val drawerShowProfile by lazy {
        PrimaryDrawerItem()
            .withName("نمایش پروفایل")
            .withIcon(R.drawable.ic_person_colored)
            .withTypeface(iransansLight)
            .withTextColor(ContextCompat.getColor(this, R.color.gray900))
            .withOnDrawerItemClickListener { _, _, _ ->
                val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                startActivity(intent)
                true
            }
    }

    /*private val drawerIncreaseBalance by lazy {
        PrimaryDrawerItem()
            .withName("اعتبار")
            .withIcon(R.drawable.ic_drawer_money)
            .withTypeface(iransansLight)
            .withTextColor(ContextCompat.getColor(this, R.color.gray900))
            .withOnDrawerItemClickListener { _, _, _ ->
                val intent = Intent(this@MainActivity, IncreaseBalanceActivity::class.java)
                startActivity(intent)
                true
            }
    }
    private val drawerShopList by lazy {
        PrimaryDrawerItem()
            .withName("لیست فروشگاه ها")
            .withIcon(R.drawable.ic_drawer_online_store)
            .withTypeface(iransansLight)
            .withTextColor(ContextCompat.getColor(this, R.color.gray900))
            .withOnDrawerItemClickListener { _, _, _ ->
                val intent = Intent(this@MainActivity, ProductListActivity::class.java)
                startActivity(intent)
                true
            }
    }*/

    private val drawerCart by lazy {
        PrimaryDrawerItem()
            .withName("سبد خرید")
            .withIcon(R.drawable.ic_drawer_shopping_cart)
            .withBadge(StringHolder(viewModel.cartItemsCount.value?.toString() ?: "0"))
            .withBadgeStyle(BadgeStyle().withTextColor(ContextCompat.getColor(this, R.color.white)))
            .withTypeface(iransansLight)
            .withTextColor(ContextCompat.getColor(this, R.color.gray900))
            .withOnDrawerItemClickListener { _, _, _ ->
                val intent = Intent(this@MainActivity, CartActivity::class.java)
                if (isCartActivityOpenedBefore) {
                    startActivity(intent)
                } else {
                    isCartActivityOpenedBefore = true
                    startActivityWithTransition(intent)
                }
                true
            }
    }
    private val drawerInviteFriends by lazy {
        PrimaryDrawerItem()
            .withName("دعوت از دوستان")
            .withIcon(R.drawable.ic_drawer_invite_friends)
            .withTypeface(iransansLight)
            .withTextColor(ContextCompat.getColor(this, R.color.gray900))
            .withOnDrawerItemClickListener { _, _, _ ->
                val intent = Intent(this@MainActivity, InviteFriendsActivity::class.java)
                startActivity(intent)
                true
            }
    }
    private val drawerAboutUs by lazy {
        PrimaryDrawerItem()
            .withName("درباره ما")
            .withIcon(R.drawable.ic_drawer_about_us)
            .withTypeface(iransansLight)
            .withTextColor(ContextCompat.getColor(this, R.color.gray900))
            .withOnDrawerItemClickListener { _, _, _ ->
                startActivity(Intent(this@MainActivity, AboutUsActivity::class.java))
                //todo override pending transition
                true
            }
    }
    private val drawerLogout by lazy {
        PrimaryDrawerItem()
            .withName("خروج از حساب کاربری")
            .withIcon(R.drawable.ic_darwer_logout)
            .withTypeface(iransansLight)
            .withTextColor(ContextCompat.getColor(this, R.color.gray900))
            .withOnDrawerItemClickListener { _, _, _ ->
                mDrawer.setSelection(drawerHome)
                mDrawer.closeDrawer()
                Handler().postDelayed({
                    alert(
                        "خروج از حساب",
                        "آیا از حساب کاربری خود خارج میشوید؟",
                        "بله، خارج میشوم",
                        "خیر",
                        null
                    ) {
                        viewModel.logout()
                    }
                }, 0)
                true
            } // خروج از حساب کاربری
    }

    //Typefaces
    private val iransansMedium: Typeface get() = (application as MyStoreApplication).iransansMedium
    private val iransansLight: Typeface get() = (application as MyStoreApplication).iransansLight
    private val belham: Typeface get() = (application as MyStoreApplication).belham

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ///////////////////////////
        //todo/////////////////////
        if (false) {
            val intent = Intent(this, DeliveryPreparationActivity::class.java)
            //intent.putExtra(Constants.INTENT_ORDER_DETAILS_ACTIVITY_ADDRESS, fakeAddresses()[0])
            startActivity(intent)
            finish()
            return
        }
        ////////////////////////////

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)


        initViews()
        subscribeObservers()

        viewModel.getData()

    }

    override fun onResume() {
        super.onResume()
        if (::mDrawer.isInitialized) {
            if (mDrawer.isDrawerOpen) {
                mDrawer.closeDrawer()
            }
            mDrawer.setSelection(drawerHome)
        }
        if (isAmazingTimerEnds) {
            frame_timer.visibility = View.INVISIBLE
        }
    }

    override fun onBackPressed() {
        when {
            mDrawer.isDrawerOpen -> {
                mDrawer.closeDrawer()
                return
            }
            isSearchShown -> {
                closeSearchBar()
                isSearchShown = false
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    private fun subscribeObservers() {
        viewModel.cartItemsCount.observe(this, Observer { count ->
            if (::mDrawer.isInitialized)
                mDrawer.updateBadge(drawerCart.identifier, StringHolder(count.toString()))
        })

        viewModel.mainPageData.observe(this, Observer { mainPageData ->
            if (mainPageData != null) {
                //recyclers
                categoryAdapter.submitList(mainPageData.categories)
                productAdapterAmazing.submitList(mainPageData.amazingProducts)
                productAdapterMostSell.submitList(mainPageData.mostSellProducts)
                productAdapterNew.submitList(mainPageData.newProducts)
                //Slider
                initSlider(mainPageData.slides)
                //toolbar buttons
                initToolbarButtons()
                crossfade(frame_main, shimmerFrame)
                Handler().postDelayed({ //mikham yeho stop nashe ux badi dare, bad az cross fade shodan in etefagh biofte behtare
                    shimmerFrame.stopShimmer()
                }, 1000)

                initTimer(mainPageData.timer)


                val updateInfo = mainPageData.appUpdateInfo
                if (updateInfo != null) {
                    if (BuildConfig.VERSION_NAME < updateInfo.version) {
                        val updateAppDialog = UpdateAppDialog(
                            this,
                            R.style.my_alert_dialog,
                            updateInfo.isForce
                        ) { isGranted, dialog ->
                            if (isGranted) {
                                val browserIntent =
                                    Intent(Intent.ACTION_VIEW, Uri.parse(updateInfo.link))
                                browserIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                                startActivity(browserIntent)
                                if (updateInfo.isForce)
                                    finish()
                            } else {
                                if (updateInfo.isForce) {
                                    toast("به علت حیاتی بودن این آپدیت باید حتما اپدیت شود")
                                } else {
                                    dialog.dismiss()
                                }
                            }
                        }
                        if (updateInfo.isForce || viewModel.shouldShowUpdateMessage()) {
                            updateAppDialog.show()
                        }
                    }
                }

            } else {
                snack(Constants.SERVER_ERROR, {
                    viewModel.refreshData()
                })
            }
        })

        viewModel.user.observe(this, Observer { user ->
            if (::mDrawer.isInitialized) {
                if (mDrawer.isDrawerOpen) {
                    mDrawer.closeDrawer()
                }
                mDrawer.setSelection(drawerHome)
            }
            initDrawer(user)
        })
    }

    private fun initTimer(timer: Long) {
        object : CountDownTimer(timer * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val units = millisToTimeString(millisUntilFinished).split(":")
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    second.text = units[3]
                    min.text = units[2]
                    hour.text = units[1]
                    days.text = units[0]
                }
            }

            override fun onFinish() {
                if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                    frame_timer.visibility = View.INVISIBLE
                } else {
                    isAmazingTimerEnds = true
                }
            }
        }.start()
    }

    private fun initSlider(slides: ArrayList<Slide>) {
        val sliderAdapter = SliderAdapter(slides)
        sliderAdapter.setSlideInteractions(SliderInteractionImpl(this))
        sliderView.setSliderAdapter(sliderAdapter)
        //
        sliderView.startAutoCycle()
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM)
        sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION)
        sliderView.indicatorSelectedColor = ContextCompat.getColor(this, R.color.primary_light)
        sliderView.indicatorUnselectedColor = Color.GRAY
        sliderView.scrollTimeInSec = 8 //set scroll delay in seconds :
    }

    private fun initViews() {
        shimmerFrame.startShimmer()
        initRecyclers()
    }

    private fun initToolbarButtons() {

        ivNavigate.setOnClickListener {
            if (!mDrawer.isDrawerOpen) {
                mDrawer.openDrawer()
            }
            viewModel.updateUserProfile()
        }

        ivCart.setOnClickListener {
            val intent = Intent(this@MainActivity, CartActivity::class.java)
            if (isCartActivityOpenedBefore) {
                startActivity(intent)
            } else {
                isCartActivityOpenedBefore = true
                startActivityWithTransition(intent)
            }
        }

        ivSearch.setOnClickListener {
            isSearchShown = true
            frame_toolbar_buttons.visibility = View.INVISIBLE
            frame_search.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startReveal(frame_search) {
                    if (etSearch.requestFocus())
                        showSoftKeyboard(etSearch)
                }
            } else {
                if (etSearch.requestFocus())
                    showSoftKeyboard(etSearch)
            }
        }

        ivClose.setOnClickListener {  //TODO closeReveal kar nemikonad??
            closeSearchBar()
        }

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (
                actionId == EditorInfo.IME_ACTION_SEARCH
            /*|| actionId == EditorInfo.IME_ACpTION_DONE ||
            event.action == KeyEvent.ACTION_DOWN ||
            event.action == KeyEvent.KEYCODE_ENTER*/
            ) {
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
                            Category(0, "جست و جو در کل", "")
                        )
                        startActivity(intent)
                    }
                }
            }
            false
        }
    }

    private fun initRecyclers() {
        //recyclers
        val productAdapterInteractions = ProductAdapterInteractionSimpleImpl(this)

        recyclerCategory.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        categoryAdapter = CategoryAdapter(CategoryAdapterInteractionImpl(this))
        recyclerCategory.adapter = categoryAdapter
        recyclerCategory.isNestedScrollingEnabled = false
        //recyclerCategory.setHasFixedSize(true)

        recyclerAmazing.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        productAdapterAmazing = ProductHorizontalAdapter(productAdapterInteractions)
        recyclerAmazing.adapter = productAdapterAmazing
        recyclerAmazing.isNestedScrollingEnabled = false
        //recyclerAmazing.setHasFixedSize(true)

        recyclerMostSell.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        productAdapterMostSell = ProductHorizontalAdapter(productAdapterInteractions)
        recyclerMostSell.adapter = productAdapterMostSell
        recyclerMostSell.isNestedScrollingEnabled = false
        //recyclerMostSell.setHasFixedSize(true)

        recyclerNewProducts.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, true)
        productAdapterNew = ProductHorizontalAdapter(productAdapterInteractions)
        recyclerNewProducts.adapter = productAdapterNew
        recyclerNewProducts.isNestedScrollingEnabled = false
        //recyclerNewProducts.setHasFixedSize(true)

        tvFullMostSellProducts.setOnClickListener {
            val intent = Intent(this@MainActivity, ProductListActivity::class.java)
            intent.putParcelableExtra(Constants.INTENT_PRODUCT_LIST_ACTIVITY_FIELD, Field.MOST_SELL)
            startActivity(intent)
        }

        tvFullNewProducts.setOnClickListener {
            val intent = Intent(this@MainActivity, ProductListActivity::class.java)
            intent.putParcelableExtra(Constants.INTENT_PRODUCT_LIST_ACTIVITY_FIELD, Field.NEW)
            startActivity(intent)
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

    private fun initDrawer(user: User?) {
        val isLoggedIn = user != null

        // PrimaryDrawerItem drawerCategory; // دسته بندی
        //PrimaryDrawerItem drawerJoinInMillionaire; // عضویت در درآمد میلیونی
        ///////////////////////////////////////////////////////
        val headerResult: AccountHeader = when (isLoggedIn) {
            true -> {
                AccountHeaderBuilder()
                    .withActivity(this)
                    .withAccountHeader(R.layout.material_drawer_header_logged_in)
                    .withSelectionListEnabledForSingleProfile(false)
                    .withHeaderBackground(R.drawable.toolbar_gradient)
                    .withNameTypeface(iransansLight)
                    .withTextColor(ContextCompat.getColor(this, R.color.white))
                    .build()
            }
            false -> {
                AccountHeaderBuilder()
                    .withActivity(this)
                    .withAccountHeader(R.layout.material_drawer_header_logged_out)
                    .withSelectionListEnabledForSingleProfile(false)
                    .withHeaderBackground(R.drawable.toolbar_gradient)
                    .withNameTypeface(iransansLight)
                    .withTextColor(ContextCompat.getColor(this, R.color.white))
                    .build()
            }
        }

        when (isLoggedIn) {
            true -> {
                tvNameDrawer = headerResult.view.findViewById(R.id.user_fullName_text)
                tvNameDrawer.typeface = iransansMedium

                val showProfileText =
                    headerResult.view.findViewById<TextView>(R.id.show_profile_text)
                showProfileText.typeface = iransansLight

                ivProfile = headerResult.view.findViewById(R.id.main_activity_profile_image)
                val drawerProfile = headerResult.view.findViewById<RelativeLayout>(R.id.text_layout)
                drawerProfile.setOnClickListener {
                    val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                    startActivity(intent)
                }
                tvNameDrawer.text = user?.name
                loadProfilePic(ivProfile, user?.profilePic)
            }
            false -> {
                val drawerLogin = headerResult.view.findViewById<TextView>(R.id.text_layout)
                drawerLogin.setOnClickListener {
                    val intent =
                        Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        val headerAppName = headerResult.view.findViewById<TextView>(R.id.header_app_name)
        headerAppName.typeface = belham
        //
        mDrawer = DrawerBuilder()
            .withDrawerGravity(Gravity.END)
            .withActivity(this)
            .withTranslucentStatusBar(true)
            .withActionBarDrawerToggle(true)
            .withAccountHeader(headerResult)
            .withDisplayBelowStatusBar(false)
            .withSelectedItem(-1)
            .build()

        if (isLoggedIn) {
            mDrawer.addItems(
                drawerHome,
                DividerDrawerItem(),
                drawerShowProfile,
                DividerDrawerItem(),
                /*drawerIncreaseBalance,
                DividerDrawerItem(),*/
                //drawerShopList,
                drawerCart,
                drawerInviteFriends,
                DividerDrawerItem(),
                drawerAboutUs,
                drawerLogout
            )
        } else {
            mDrawer.addItems(
                drawerHome,
                DividerDrawerItem(),
                //drawerShopList,
                drawerCart,
                DividerDrawerItem(),
                drawerAboutUs
            )
        }
    }

    override fun onInternetUnavailable() {
        NoNetworkDialog(this, R.style.my_alert_dialog).show()
    }
}
