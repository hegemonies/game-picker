package ru.twoshoes.gamepicker.router

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import ru.twoshoes.gamepicker.service.game.GameService

@RestController
class GameController(
    private val gameService: GameService
) {

    @PostMapping("/games")
    suspend fun getGames(): Unit {
        gameService.getGames()
    }

    @GetMapping("/games/{game_id}")
    suspend fun getGame(@PathVariable(value = "game_id") gameId: Long) =
        gameService.getGame(gameId)
}
