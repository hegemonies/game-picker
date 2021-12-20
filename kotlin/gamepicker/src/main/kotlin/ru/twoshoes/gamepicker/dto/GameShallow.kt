package ru.twoshoes.gamepicker.dto

data class GameShallow(
    val id: Long,
    val title: String,
    val description: String,
    val tags: List<TagDto>,
    val platforms: List<PlatformDto>,
    val prices: List<PriceDto>,
    val genres: List<GenreDto>,
    val releaseDate: String,
    val developer: DeveloperDto,
    val publisher: PublisherDto
)
