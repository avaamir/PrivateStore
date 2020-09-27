package com.amir.ir.privatestore.utils

import com.amir.ir.privatestore.R
import com.amir.ir.privatestore.UserConfigs
import com.amir.ir.privatestore.models.*
import com.amir.ir.privatestore.models.enums.OrderStatus
import com.amir.ir.privatestore.models.enums.PaymentMethod
import com.amir.ir.privatestore.models.reponses.CategoryPageResponse
import com.amir.ir.privatestore.models.reponses.MainPageResponse
import com.amir.ir.privatestore.models.requests.*
import kotlinx.android.parcel.RawValue


/*
* todo baraye estefade az in fake data deghat shavad chun image ha az
*  pushe drawable miad bayad dar glide helper baraye test in 2 khat ezafe she
*  val _picUrl = picUrl?.toInt()
*  .load(_picUrl)
* */

fun fakeUser(): User = User(
    _id = 1,
    name = "امیرحسین مهدی پور",
    phone = "09362163813",
    token = "1293aaa9210123",
    profilePic = R.drawable.ic_profile_sample.toString()
)

fun fakeMainPageResponse() = MainPageResponse(
    slides = fakeSlides(),
    categories = fakeCategories(),
    amazingProducts = fakeProducts(),
    newProducts = fakeProducts(),
    mostSellProducts = fakeProducts(),
    timer =  60000,
    appUpdateInfo = null
)

fun fakeCategoryPageResponse() = CategoryPageResponse(
    id = 1,
    title = "آرایشی و بهداشتی",
    slides = fakeSlides(),
    subCategories = fakeSubCategories(),
    mostSellProducts = fakeProducts(),
    offeredProducts = fakeProducts(),
    newProducts = fakeProducts()
)

fun fakeCategories(): ArrayList<Category> = arrayListOf(
    Category(1, "سوپر مارکت", R.drawable.ic_category_super_market_sample.toString()),
    Category(2, "آرایشی و بهداشتی", R.drawable.ic_category_condom_sample.toString()),
    Category(3, "لوازم جانبی", R.drawable.ic_category_headphone_sample.toString())
)

fun fakeSubCategories(): ArrayList<Category> = arrayListOf(
    Category(4, "زناشویی", R.drawable.ic_category_man_woman_sample.toString()),
    Category(5, "مخصوص آقایان", R.drawable.ic_category_just_men_sample.toString()),
    Category(6, "مخصوص بانوان", R.drawable.ic_category_just_woman_sample.toString())
)


fun fakeSlides() =
    arrayListOf(
        Slide(
            1,
            "تیتر1",
            "https://alocondom.com/wp-content/uploads/2014/12/fori.jpg",
            1,
            1,
            "https://google.com"
        ),
        Slide(
            2,
            "تیتر2",
            "https://alocondom.com/wp-content/uploads/2014/12/%D8%AA%D8%AE%D9%81%DB%8C%D9%81.jpg",
            2,
            2,
            "https://google.com"
        ),
        Slide(
            3,
            "تیتر3",
            "https://alocondom.com/wp-content/uploads/2014/12/%D8%AA%D8%AD%D9%88%DB%8C%D9%84-%D9%85%D8%AD%D8%B1%D9%85%D8%A7%D9%86%D9%87-%DA%A9%D8%A7%D9%86%D8%AF%D9%88%D9%85.jpg",
            1,
            3,
            "https://google.com"
        ),
        Slide(
            4,
            "تیتر4",
            "https://alocondom.com/wp-content/uploads/2014/12/%D8%AA%D8%AE%D9%81%DB%8C%D9%81.jpg",
            3,
            4,
            "https://google.com"
        )
    )

fun fakeProducts(): ArrayList<Product> {
    val products = ArrayList<Product>()
    for (i in 1 until 5) {
        products.add(
            Product(
                pid = i,
                name = "محصول $i",
                desc = Constants.LARGE_TEST_TEXT,
                image = R.drawable.ic_logo_144dp.toString(),
                images = fakeProductImages(),
                mainPrice = "${i + 1}0000",
                discountPrice = "${i}0000",
                discountPercent = "10",
                totalQuantity = 4,
                colors = fakeColors(),
                sizes = null,
                isTagged = false
            )
        )
    }
    for (i in 1 until 5) {
        products.add(
            Product(
                pid = i + 5,
                name = "محصول $i",
                desc = Constants.LARGE_TEST_TEXT,
                image = R.drawable.product_condom_sample_1.toString(),
                mainPrice = "${i + 1}0000",
                discountPrice = "${i + 1}0000",
                discountPercent = "10",
                totalQuantity = 4,
                images = fakeProductImages(),
                colors = null,
                sizes = fakeSize(),
                isTagged = false
            )
        )
    }
    return products
}

fun fakeProductImages() = arrayListOf(
    R.drawable.product_condom_sample_1.toString(),
    R.drawable.product_condom_sample_2.toString()
)

fun fakeSize(): @RawValue ArrayList<Size> = arrayListOf(
    Size(1, 3, "M", "2000", "2000"),
    Size(2, 4, "L", "4000", "1000"),
    Size(3, 2, "XL", "5000", "1000"),
    Size(4, 6, "3XL", "1000", "1000"),
    Size(5, 6, "XXL", "2500", "1000")
)

fun fakeColors(): @RawValue ArrayList<Color> = arrayListOf(
    Color(1, 3, "قرمز", "#ff0000"),
    Color(2, 4, "آبی","#0000ff"),
    Color(3, 2, "بنفش","00ffff"),
    Color(4, 6,  "صورتی","#0020100")
)


fun fakeOrders() = arrayListOf(
    Order(1, "20/4/96", "30000", OrderStatus.Preparation.strType, "MYSHOP-92812jcd912"),
    Order(2, "20/4/96", "40000", OrderStatus.Delivered.strType, "MYSHOP-122812jcd911"),
    Order(3, "20/4/96", "3000", OrderStatus.Failed.strType, "MYSHOP-754812jcd914"),
    Order(5, "20/4/96", "60000", OrderStatus.Failed.strType, "MYSHOP-12812jcd912"),
    Order(6, "20/4/96", "70000", OrderStatus.Delivered.strType, "MYSHOP-62812jcd912"),
    Order(7, "20/4/96", "10000", OrderStatus.Preparation.strType, "MYSHOP-754812jcd914"),
    Order(8, "20/4/96", "90000", OrderStatus.Preparation.strType, "MYSHOP-667312jcd913")
)


fun fakeAddresses(): java.util.ArrayList<Address> {
    val addresses = java.util.ArrayList<Address>()
    for (i in 0..9) {
        addresses.add(
            Address(
                id = i,
                originalTitle = "خانه$i",
                ownerName = "امیرحسین مهدی پور$i",
                address = "فارس - شیراز - بلوار مدرس$i",
                pelak = "98248124$i",
                postCode = "5432344$i",
                lat = "",
                lng = "",
                city = "شیراز",
                province = "فارس"
            )
        )
    }
    return addresses
}


fun fakeComments() = arrayListOf(
    Comment(1, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 4.5f, "20/1/99", R.drawable.ic_profile_sample.toString()),
    Comment(2, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 3.5f, "20/1/99"),
    Comment(3, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 1.5f, "20/1/99"),
    Comment(4, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 1f, "20/1/99"),
    Comment(5, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 2.5f, "20/1/99"),
    Comment(6, "امیرحسین مهدی پور", Constants.LARGE_TEST_TEXT, 4.5f, "20/1/99"),
    Comment(7, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 4.5f, "20/1/99"),
    Comment(7, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 4.5f, "20/1/99"),
    Comment(7, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 4.5f, "20/1/99"),
    Comment(7, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 4.5f, "20/1/99"),
    Comment(7, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 4.5f, "20/1/99"),
    Comment(7, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 4.5f, "20/1/99"),
    Comment(7, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 4.5f, "20/1/99"),
    Comment(7, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 4.5f, "20/1/99"),
    Comment(7, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 4.5f, "20/1/99"),
    Comment(7, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 4.5f, "20/1/99"),
    Comment(7, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 4.5f, "20/1/99"),
    Comment(7, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 4.5f, "20/1/99"),
    Comment(7, "امیرحسین مهدی پور", "بسیار عالی. من که راضیم", 4.5f, "20/1/99")
)


fun fakeCartItem(): ArrayList<CartItem> {
    val items = ArrayList<CartItem>()
    for (i in 1..5) {
        items.add(
            CartItem(
                i,
                i,
                "کاندوم خاردار کدکس",
                5,
                i,
                "بنفش",
                "#222222",
                "20000",
                "20000",
                0,
                null,
                R.drawable.product_condom_sample_2.toString(),
                10
            )
        )
    }
    for (i in 1..5) {
        items.add(
            CartItem(
                i + 5,
                i,
                "کاندوم خاردار کدکس",
                i,
                0,
                null,
                null,
                "20000",
                "10000",
                1,
                "XL",
                R.drawable.product_condom_sample_2.toString(),
                10
            )
        )
    }


    return items
}


fun fakeFavorites(userId: Int) = fakeProducts().map { product ->
    Favorite(
        pid = product.pid,
        userId = userId,
        name = product.name,
        image = product.image
    )
}

fun fakeOrderDetails() = GetOrderDetailsResponse(
    address = fakeAddresses()[0],
    _ordersListItem = fakeOrderListItems(),
    totalMainPrice = "60000",
    totalDiscountAmount = "10000",
    deliveryPrice = "5000",
    _deliveryMethodName = "پیک",
    _paymentMethod = 0,
    _paymentStatus = 1,
    totalPayment = "75000",
    error = false,
    errorMsg = null,
    taxPrice = "0"
)

fun fakeOrderListItems(): ArrayList<OrderListItem> {
    val items = ArrayList<OrderListItem>()
    for (i in 1..5) {
        items.add(
            OrderListItem(
                i,
                "کاندوم خاردار کدکس",
                5,
                "بنفش",
                "20000",
                "20000",
                "بزرگ",
                R.drawable.product_condom_sample_2.toString()
            )
        )
    }
    return items
}

fun fakeSignUpResponse(error: Boolean) = SignUpResponse(
    id = 8,
    error = error,
    errorMsg = "شماره تلفن وارد شده قبلا ثبت شده است"
)

fun fakeOnLoggedInResponse(error: Boolean) = OnLoggedInResponse(
    id = 1,
    name = "امیرحسین مهدی پور",
    phone = "09362163813",
    token = "1293aaa9210123",
    profilePic = R.drawable.ic_profile_sample.toString(),
    error = error,
    errorMsg = "نام کاربری یا پسورد شما صحیح نمیباشد"
)

fun fakeReSendSMSResponse(error: Boolean) = ReSendSMSResponse(
    error = error,
    errorMsg = "خطای سرور. لطفا بعدا تلاش کنید"
)

fun fakeForgotPassResponse(error: Boolean) = ForgotPasswordResponse(
    error = error,
    errorMsg = "خطای سرور. لطفا بعدا تلاش کنید"
)

fun fakePaymentResult(hasError: Boolean) =
    if (hasError) {
        CheckPaymentResultResponse(
            error = false,
            errorMsg = "پرداخت نشد",
            isPaymentOk = false
        )
    } else {
        CheckPaymentResultResponse(
            error = false,
            errorMsg = "پرداخت شد",
            isPaymentOk = true
        )
    }


fun fakeSubmitOrderResponse(hasError: Boolean, request: SubmitOrderRequest) = if (hasError) {
    SubmitOrderResponse(0, "", true, "خطای فیک سرور")
} else {
    val isCash = (request.paymentMethod == PaymentMethod.Cash.id)
    val isBike = (request.sendMethod == 2)
    if (isBike && isCash) {
        SubmitOrderResponse(12, null, false, null)
    } else {
        SubmitOrderResponse(12, "https://google.com", false, null)
    }

}


fun fakeCheckDiscountCodeResponse(hasError: Boolean) = if (hasError) {
    CheckDiscountCodeResponse(
        id = 1,
        amount = 2000,
        minApplicablePrice = 5000,
        _type = 1,
        error = true,
        errorMsg = null
    )
} else {
    CheckDiscountCodeResponse(
        id = 1,
        amount = 2000,
        minApplicablePrice = 5000,
        _type = 1,
        error = false,
        errorMsg = "این کد تخفیف در این بازه زمانی تاثیری ندارد"
    )
}

fun fakeGetDeliveryMethods(hasError: Boolean) = if (hasError) {
    GetDeliveryMethodsResponse(
        methods = listOf(),
        error = true,
        errorMsg = "خطای فیک گرفتن روش های ارسال از سرور",
        paymentMethods = listOf(1, 2),
        taxPrice = "1000"
    )
} else {
    GetDeliveryMethodsResponse(
        methods = listOf(
            DeliveryMethod(1, "پست سفارشی", "20000"),
            DeliveryMethod(2, "پیک", "5000"),
            DeliveryMethod(3, "پست پیشتاز", "10000")
        ),
        error = false,
        errorMsg = null,
        paymentMethods = listOf(1, 2),
        taxPrice = "1000"
    )
}


fun fakeMessages() =
    listOf(
    Message(
        1,
        "پیک",
        "سلام. با عرض پوزش با مقداری تاخیر خواهم رسید.",
        UserConfigs.userVal?.profilePic
    ),
    Message(2, "پیک", "سلام. من درب منزل شما هستم", UserConfigs.userVal?.profilePic),
    Message(3, "شاپ 69", "از خرید شما متشکریم", UserConfigs.userVal?.profilePic)
)