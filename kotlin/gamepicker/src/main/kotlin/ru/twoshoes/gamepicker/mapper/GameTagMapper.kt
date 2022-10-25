package ru.twoshoes.gamepicker.mapper

import ru.twoshoes.gamepicker.dto.TagDto
import ru.twoshoes.gamepicker.model.tag.Tag
import ru.twoshoes.gamepicker.model.tag.TagsGames

fun Tag.toDto() =
    TagDto(
        id = this.id.toString(),
        name = this.name
    )

fun List<TagsGames>.toDto() =
    this.map { tagsGames ->
        tagsGames.tag.toDto()
    }
