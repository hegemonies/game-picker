package ru.twoshoes.gamepicker.router

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.web.bind.annotation.*
import ru.twoshoes.gamepicker.dto.getgames.GetGamesRequestDto
import ru.twoshoes.gamepicker.dto.getgames.GetGamesResponseDto
import ru.twoshoes.gamepicker.service.game.GameService

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
    suspend fun getGame(@PathVariable(value = "game_id") gameId: Long) =
        gameService.getGame(gameId)
}
