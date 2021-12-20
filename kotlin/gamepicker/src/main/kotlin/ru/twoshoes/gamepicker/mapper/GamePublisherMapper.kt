package ru.twoshoes.gamepicker.mapper

import ru.twoshoes.gamepicker.dto.PublisherDto
import ru.twoshoes.gamepicker.model.Publisher

fun Publisher.toDto() =
    PublisherDto(
        id = this.id.toString(),
        name = this.name
    )
