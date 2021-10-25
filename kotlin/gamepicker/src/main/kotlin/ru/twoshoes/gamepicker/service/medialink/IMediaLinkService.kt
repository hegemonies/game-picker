package ru.twoshoes.gamepicker.service.medialink

import arrow.core.Either
import ru.twoshoes.gamepicker.model.MediaLink

interface IMediaLinkService {

    fun saveMediaLinks(mediaLinks: List<String>, gameId: Long): Either<Throwable, List<MediaLink>>
}
