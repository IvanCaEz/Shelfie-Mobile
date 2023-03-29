package com.example.shelfie_app.model



data class User(
    var idUser: String,
    var name: String,
    var email: String,
    var password: String,
    var userType: UserType,
    var borrowedBooksCounter: Int,
    var bookHistory: MutableSet<String>, // <- Cambiar a set de Ints
    var banned: Boolean,
    var userImage:  String
)