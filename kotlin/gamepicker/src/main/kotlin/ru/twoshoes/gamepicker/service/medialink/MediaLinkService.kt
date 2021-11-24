package ru.twoshoes.gamepicker.service.medialink

import arrow.core.Either
import org.springframework.stereotype.Service
import ru.twoshoes.gamepicker.consts.MediaType
import ru.twoshoes.gamepicker.model.MediaLink
import ru.twoshoes.gamepicker.repository.MediaLinkRepository

@Service
class MediaLinkService(
    private val mediaLinkRepository: MediaLinkRepository
) : IMediaLinkService {

    override fun saveMediaLinks(mediaLinks: List<Pair<String, MediaType>>, gameId: Long): Either<Throwable, List<MediaLink>> =
        Either.catch {
            mediaLinkRepository.saveAll(
                mediaLinks.map { (mediaLink, mediaType) ->
                    MediaLink(
                        mediaLink = mediaLink,
                        gameId = gameId,
                        mediaType = mediaType.number
                    )
                }
            )
        }
}
