package ru.twoshoes.gamepicker.router

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.twoshoes.gamepicker.dto.GameDto
import ru.twoshoes.gamepicker.dto.getgames.GetGamesRequestDto
import ru.twoshoes.gamepicker.dto.getgames.GetGamesResponseDto
import ru.twoshoes.gamepicker.service.game.GameService
import kotlin.system.measureTimeMillis

@RestController
class GameController(
    private val gameService: GameService
) {

    @PostMapping("/games")
    suspend fun getGames(@RequestBody request: GetGamesRequestDto): GetGamesResponseDto {
        return withContext(Dispatchers.IO) {
            gameService.getGames(request)
        }
    }

    @GetMapping("/games/{game_id}")
    suspend fun getGame(@PathVariable(value = "game_id") gameId: Long): GameDto {
        var response: GameDto

        val elapsed = measureTimeMillis {
            response = gameService.getGame(gameId)
        }

        logger.debug("Get game by id #$gameId took $elapsed ms")

        return response
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }
}
