package ru.twoshoes.gamepicker.service

import arrow.core.Either
import ru.twoshoes.gamepicker.dto.GetAllSteamGamesResponse

interface ISteamService {

    suspend fun getHtmlPage(gameId: Long): Either<Throwable, String>

    suspend fun getAllGames(): Either<Throwable, GetAllSteamGamesResponse>
}
