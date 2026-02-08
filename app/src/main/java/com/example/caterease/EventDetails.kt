package com.example.caterease

import java.io.Serializable

data class EventDetails(
    val eventDate: String,
    val eventType: String,
    val numPeople: String,
    val address: String,
    val phoneNumber: String
) : Serializable
