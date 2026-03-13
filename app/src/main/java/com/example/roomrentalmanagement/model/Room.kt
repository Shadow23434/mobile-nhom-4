package com.example.roomrentalmanagement.model

data class Room(
    var id: String,
    var name: String,
    var price: Double,
    var isAvailable: Boolean = true,
    var tenantName: String = "",
    var tenantPhone: String = ""
)
