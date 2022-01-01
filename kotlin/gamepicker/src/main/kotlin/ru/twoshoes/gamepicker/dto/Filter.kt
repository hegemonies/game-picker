package ru.twoshoes.gamepicker.dto

data class Filter(
    val name: String,
    val genres: List<String>,
    val platforms: List<String>,
    val tags: List<String>,
    val categories: List<String>,
    val developers: List<String>,
    val publishers: List<String>
)
