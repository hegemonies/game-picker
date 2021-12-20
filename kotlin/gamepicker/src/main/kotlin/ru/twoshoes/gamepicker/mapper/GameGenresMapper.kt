package ru.twoshoes.gamepicker.mapper

import ru.twoshoes.gamepicker.dto.GenreDto
import ru.twoshoes.gamepicker.model.genre.Genre
import ru.twoshoes.gamepicker.model.genre.GenresGames

fun Genre.toDto() =
    GenreDto(
        id = this.id.toString(),
        name = this.name
    )

fun List<GenresGames>.toDto() =
    this.map { genresGames ->
        genresGames.genre.toDto()
    }
