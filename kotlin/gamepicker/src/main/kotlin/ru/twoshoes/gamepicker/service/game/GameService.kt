package ru.twoshoes.gamepicker.service.game

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.twoshoes.gamepicker.dto.GameDto
import ru.twoshoes.gamepicker.dto.getgames.GetGamesRequestDto
import ru.twoshoes.gamepicker.dto.getgames.GetGamesResponseDto
import ru.twoshoes.gamepicker.mapper.toDto
import ru.twoshoes.gamepicker.mapper.toShallow
import ru.twoshoes.gamepicker.repository.*
import javax.transaction.Transactional

@Service
class GameService(
    private val gameRepository: GameRepository,
    private val priceRepository: PriceRepository,
    private val mediaLinkRepository: MediaLinkRepository,
    private val tagsGamesRepository: TagsGamesRepository,
    private val genresGamesRepository: GenresGamesRepository,
    private val platformsGamesRepository: PlatformsGamesRepository
) {

    @Transactional
    suspend fun getGames(request: GetGamesRequestDto): GetGamesResponseDto {
        val games = gameRepository.findAll(
            PageRequest.of(request.paginator.pageNumber, request.paginator.pageSize)
        )

        return GetGamesResponseDto(
            games = games.content.toShallow(),
            size = games.totalElements.toInt()
        )
    }

    suspend fun getGame(gameId: Long): GameDto {
        val game = withContext(Dispatchers.IO) {
            gameRepository.findByIdOrNull(gameId)
        } ?: throw RuntimeException("No found game by id #$gameId")

        val prices = withContext(Dispatchers.IO) {
            async {
                priceRepository.findAllByGameId(gameId).toDto()
            }
        }

        val mediaLinks = withContext(Dispatchers.IO) {
            async {
                mediaLinkRepository.findAllByGameId(gameId).toDto()
            }
        }

        val tags = withContext(Dispatchers.IO) {
            async {
                tagsGamesRepository.findAllTagsByGameId(gameId).toDto()
            }
        }

        val genres = withContext(Dispatchers.IO) {
            async {
                genresGamesRepository.findAllByGameId(gameId).toDto()
            }
        }

        val platforms = withContext(Dispatchers.IO) {
            async {
                platformsGamesRepository.findAllByGameId(gameId).toDto()
            }
        }

        return GameDto(
            id = game.id,
            title = game.title,
            description = game.description,
            tags = tags.await(),
            platforms = platforms.await(),
            prices = prices.await(),
            genres = genres.await(),
            releaseDate = game.releaseDate,
            developer = game.developer.toDto(),
            publisher = game.publisher.toDto(),
            mediaLinks = mediaLinks.await(),
            systemRequirements = listOf()
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }
}
