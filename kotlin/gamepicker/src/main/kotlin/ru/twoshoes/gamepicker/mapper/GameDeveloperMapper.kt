package ru.twoshoes.gamepicker.mapper

import ru.twoshoes.gamepicker.dto.DeveloperDto
import ru.twoshoes.gamepicker.model.Developer

fun Developer.toDto() =
    DeveloperDto(
        id = this.id.toString(),
        name = this.name
    )
