package ru.twoshoes.gamepicker.dto.getgames

import ru.twoshoes.gamepicker.dto.Filter
import ru.twoshoes.gamepicker.dto.Paginator
import ru.twoshoes.gamepicker.dto.Sort

data class GetGamesRequestDto(
    val filter: Filter?,
    val sort: Sort?,
    val paginator: Paginator
)
