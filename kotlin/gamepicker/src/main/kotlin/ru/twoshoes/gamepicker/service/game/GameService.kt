package ru.twoshoes.gamepicker.service.game

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.twoshoes.gamepicker.dto.GameDto
import ru.twoshoes.gamepicker.dto.GameShallow
import ru.twoshoes.gamepicker.mapper.toDto
import ru.twoshoes.gamepicker.repository.*

@Service
class GameService(
    private val gameRepository: GameRepository,
    private val priceRepository: PriceRepository,
    private val mediaLinkRepository: MediaLinkRepository,
    private val tagsGamesRepository: TagsGamesRepository,
    private val genresGamesRepository: GenresGamesRepository,
    private val platformsGamesRepository: PlatformsGamesRepository
) {

    suspend fun getGames(): List<GameShallow> {
        return listOf()
    }

    suspend fun getGame(gameId: Long): GameDto {
        val game = withContext(Dispatchers.IO) {
            gameRepository.findByIdOrNull(gameId)
        } ?: throw RuntimeException("No found game by id #$gameId")

        val prices = withContext(Dispatchers.IO) {
            priceRepository.findAllByGameId(gameId).toDto()
        }

        val mediaLinks = withContext(Dispatchers.IO) {
            mediaLinkRepository.findAllByGameId(gameId).toDto()
        }

        val tags = withContext(Dispatchers.IO) {
            tagsGamesRepository.findAllTagsByGameId(gameId).toDto()
        }

        val genres = withContext(Dispatchers.IO) {
            genresGamesRepository.findAllByGameId(gameId).toDto()
        }

        val platforms = withContext(Dispatchers.IO) {
            platformsGamesRepository.findAllByGameId(gameId).toDto()
        }

        return GameDto(
            id = game.id,
            title = game.title,
            description = game.description,
            tags = tags,
            platforms = platforms,
            prices = prices,
            genres = genres,
            releaseDate = game.releaseDate,
            developer = game.developer.toDto(),
            publisher = game.publisher.toDto(),
            mediaLinks = mediaLinks,
            systemRequirements = listOf()
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }
}
