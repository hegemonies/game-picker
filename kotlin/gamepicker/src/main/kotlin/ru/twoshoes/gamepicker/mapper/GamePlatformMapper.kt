package ru.twoshoes.gamepicker.mapper

import ru.twoshoes.gamepicker.dto.PlatformDto
import ru.twoshoes.gamepicker.model.platform.Platform
import ru.twoshoes.gamepicker.model.platform.PlatformsGames

fun Platform.toDto() =
    PlatformDto(
        id = this.id.toString(),
        name = this.name
    )

fun List<PlatformsGames>.toDto() =
    this.map { platformsGames ->
        platformsGames.platform.toDto()
    }
