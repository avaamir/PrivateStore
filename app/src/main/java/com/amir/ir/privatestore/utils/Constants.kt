package com.amir.ir.privatestore.utils

object Constants {
    val INTENT_SEARCH_MAP_ACTIVITY_RESULT_ADDRESS by lazy { "res_add" }
    private val packageName by lazy { "com.amir.ir.privatestore.myStoreApplication" }
    val ACTION_PAYMENT_RESULT by lazy { "com.amir.ir.privatestore.paymentcallback" }


    val ACTION_PAYMENT_RESULT_DATA by lazy { "paymentResult" }

    //broadcast actions
    val ACTION_ON_PAYMENT_SUCCESS_FINISH_ALL by lazy { "${packageName}/finish_all" }
    val ACTION_NO_INTERNET by lazy { "$packageName/no-net" }
    val ACTION_USER_STATE_CHANGED by lazy { "$packageName/user_state_changed" }
    val ACTION_USER_STATE_CHANGED_USER by lazy { "user" }

    //intents
    val INTENT_SEARCH_MAP_ACTIVITY_REQ_FOR_RESULT by lazy { 512 }
    val INTENT_SEARCH_MAP_ACTIVITY_LAT_LNG by lazy { "latlng" }
    val INTENT_LOGIN_ACTIVITY_REQ_LOGIN by lazy { 1413 }
    val INTENT_PROFILE_ACTIVITY_NEW_MESSAGE by lazy { "prof_mess" }
    val INTENT_PROFILE_ACTIVITY_PAGE by lazy { "page" }
    val INTENT_CART_ACTIVITY_PRODUCT by lazy { "product" }
    val INTENT_MAP_ACTIVITY_ADDRESS by lazy { "address" }
    val INTENT_ORDER_DETAILS_ACTIVITY_ADDRESS by lazy { "address" }
    val INTENT_ADD_ADDRESS_ACTIVITY_ADDRESS by lazy { "address" }
    val INTENT_CATEGORY_ACTIVITY_CAT_ID by lazy { "cid" }
    val INTENT_TRACK_ORDER_ACTIVITY_ORDER by lazy { "order" }
    val MSG_LIST_COMPLETED by lazy { "همه محصولات نمایش داده شد" }
    val INTENT_CATEGORY_ACTIVITY_CATEGORY by lazy { "cid" }
    val INTENT_PRODUCT_DETAILS_ACTIVITY_PRODUCT by lazy { "product" }
    val INTENT_PRODUCT_DETAILS_ACTIVITY_PID by lazy { "pid" }
    val INTENT_PRODUCT_LIST_ACTIVITY_CAT by lazy { "category" }
    val INTENT_PRODUCT_LIST_ACTIVITY_FIELD by lazy { "fieldid" }
    val INTENT_PRODUCT_LIST_ACTIVITY_ORDER_ID by lazy { "order_id_enum" }
    val INTENT_PRODUCT_LIST_ACTIVITY_SEARCH_KEY by lazy { "searchKey" }
    val INTENT_COMMENT_ACTIVITY_PRODUCT by lazy { "pid" }
    //////////////////////////////////////////////////////////////////////////


    val LARGE_TEST_TEXT by lazy { "لورم ایپسوم یا طرح\u200Cنما (به انگلیسی: Lorem ipsum) به متنی آزمایشی و بی\u200Cمعنی در صنعت چاپ، صفحه\u200Cآرایی و طراحی گرافیک گفته می\u200Cشود. طراح گرافیک از این متن به عنوان عنصری از ترکیب بندی برای پر کردن صفحه و ارایه اولیه شکل ظاهری و کلی طرح سفارش گرفته شده استفاده می نماید، تا از نظر گرافیکی نشانگر چگونگی نوع و اندازه فونت و ظاهر متن باشد. معمولا طراحان گرافیک برای صفحه\u200Cآرایی، نخست از متن\u200Cهای آزمایشی و بی\u200Cمعنی استفاده می\u200Cکنند تا صرفا به مشتری یا صاحب کار خود نشان دهند که صفحه طراحی یا صفحه بندی شده بعد از اینکه متن در آن قرار گیرد چگونه به نظر می\u200Cرسد و قلم\u200Cها و اندازه\u200Cبندی\u200Cها چگونه در نظر گرفته شده\u200Cاست. از آنجایی که طراحان عموما نویسنده متن نیستند و وظیفه رعایت حق تکثیر متون را ندارند و در همان حال کار آنها به نوعی وابسته به متن می\u200Cباشد آنها با استفاده از محتویات ساختگی، صفحه گرافیکی خود را صفحه\u200Cآرایی می\u200Cکنند تا مرحله طراحی و صفحه\u200Cبندی را به پایان برند." }

    val SERVER_ERROR by lazy { "خطا در برقراری ارتباط با سرور" }

    val UNAUTHORIZED_MSG by lazy { "حساب کاربری شما در دستگاه دیگری فعال است. لطفا دوباره وارد شوید" }


}