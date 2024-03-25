package com.example.skycast.alert.model.dto

data class Alert(
    val sender_name: String,
    val event: String,
    val start: Int,
    val end: Int,
    val description: String,
    val tags: List<String>
)