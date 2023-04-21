package com.example.shelfie_app.model

import android.provider.ContactsContract.CommonDataKinds.StructuredName


data class User(
    var idUser: String,
    var name: String,
    var userName: String,
    var description: String,
    var email: String,
    var password: String,
    var userType: UserType,
    var borrowedBooksCounter: Int,
    var bookHistory: Set<Int>,
    var banned: Boolean,
    var userImage:  String
)