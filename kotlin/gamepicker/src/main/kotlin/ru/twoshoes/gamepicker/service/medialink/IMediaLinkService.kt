package ru.twoshoes.gamepicker.service.medialink

import arrow.core.Either
import ru.twoshoes.gamepicker.consts.MediaType
import ru.twoshoes.gamepicker.model.MediaLink

interface IMediaLinkService {

    fun saveMediaLinks(mediaLinks: List<Pair<String, MediaType>>, gameId: Long): Either<Throwable, List<MediaLink>>
}
