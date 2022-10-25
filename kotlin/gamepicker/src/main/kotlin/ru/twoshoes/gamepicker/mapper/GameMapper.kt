package ru.twoshoes.gamepicker.mapper

import ru.twoshoes.gamepicker.dto.GameShallow
import ru.twoshoes.gamepicker.model.Game

fun Game.toShallow() =
    GameShallow(
        id = this.id,
        title = this.title,
        description = this.description,
        tags = this.tags.toDto(),
        platforms = this.platforms.toDto(),
        prices = listOf(),
        genres = this.genres.toDto(),
        releaseDate = this.releaseDate,
        developer = this.developer.toDto(),
        publisher = this.publisher.toDto()
    )

fun List<Game>.toShallow() =
    this.map { it.toShallow() }
