package ru.twoshoes.gamepicker.service.medialink

import arrow.core.Either
import org.springframework.stereotype.Service
import ru.twoshoes.gamepicker.model.MediaLink
import ru.twoshoes.gamepicker.repository.MediaLinkRepository

@Service
class MediaLinkService(
    private val mediaLinkRepository: MediaLinkRepository
) : IMediaLinkService {

    override fun saveMediaLinks(mediaLinks: List<String>, gameId: Long): Either<Throwable, List<MediaLink>> =
        Either.catch {
            mediaLinkRepository.saveAll(
                mediaLinks.map { mediaLink ->
                    MediaLink(
                        mediaLink = mediaLink,
                        gameId = gameId
                    )
                }
            )
        }
}
