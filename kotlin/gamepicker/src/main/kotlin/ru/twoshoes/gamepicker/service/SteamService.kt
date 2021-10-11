package ru.twoshoes.gamepicker.service

import arrow.core.Either
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import ru.twoshoes.gamepicker.configuration.property.SteamProperty
import ru.twoshoes.gamepicker.dto.GetAllSteamGamesResponse

@Service
class SteamService(
    private val steamProperty: SteamProperty
) : ISteamService {

    private val webClient by lazy {
        WebClient.create(steamProperty.baseUrl)
    }

    override suspend fun getHtmlPage(gameId: Long): Either<Throwable, String> =
        Either.catch {
            webClient.get()
                .uri("/$gameId")
                .retrieve()
                .bodyToMono<String>()
                .awaitSingle()
        }


    override suspend fun getAllGames(): Either<Throwable, GetAllSteamGamesResponse> {
        val url = "/ISteamApps/GetAppList/v0002/"

        return Either.catch {
            webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono<GetAllSteamGamesResponse>()
                .awaitFirst()
        }
    }
}
