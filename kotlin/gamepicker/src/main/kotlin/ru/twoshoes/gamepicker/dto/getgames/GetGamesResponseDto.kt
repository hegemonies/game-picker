package ru.twoshoes.gamepicker.dto.getgames

import ru.twoshoes.gamepicker.dto.GameShallow

data class GetGamesResponseDto(
    val games: List<GameShallow>,
    val size: Int
)
