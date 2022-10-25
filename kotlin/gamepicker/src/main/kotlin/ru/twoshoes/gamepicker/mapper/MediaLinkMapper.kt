package ru.twoshoes.gamepicker.mapper

import ru.twoshoes.gamepicker.consts.MediaType
import ru.twoshoes.gamepicker.dto.MediaLinkDto
import ru.twoshoes.gamepicker.model.MediaLink

fun MediaLink.toDto() =
    MediaLinkDto(
        mediaLink = this.mediaLink,
        type = MediaType.valueOf(this.mediaType)!!.name
    )

fun List<MediaLink>.toDto() =
    this.map { mediaLink ->
        mediaLink.toDto()
    }
