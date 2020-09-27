package com.amir.ir.privatestore.models.enums

enum class OrderStatus(val strType: String) {
    Delivered("موفق"),
    Preparation("در حال ارسال"),
    Failed("نا موفق"),
    Delivering("")
}