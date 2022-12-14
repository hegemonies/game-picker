package ru.twoshoes.gamepicker.service.steam

import arrow.core.Either
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import ru.twoshoes.gamepicker.configuration.property.SteamProperty
import ru.twoshoes.gamepicker.dto.GetAllSteamGamesResponse

@Service
class SteamService(
    private val steamProperty: SteamProperty
) : ISteamService {

    private val storeWebClient by lazy {
        WebClient.builder()
            .exchangeStrategies(
                ExchangeStrategies.builder()
                    .codecs { configurer ->
                        configurer.defaultCodecs()
                            .maxInMemorySize(32 * 1024 * 1024)
                    }
                    .build()
            )
            .baseUrl(steamProperty.storeUrl)
            .build()
    }

    private val apiWebClient by lazy {
        WebClient.builder()
            .exchangeStrategies(
                ExchangeStrategies.builder()
                    .codecs { configurer ->
                        configurer.defaultCodecs()
                            .maxInMemorySize(32 * 1024 * 1024)
                    }
                    .build()
            )
            .baseUrl(steamProperty.apiUrl)
            .build()
    }

    override suspend fun getHtmlPage(gameId: Long): Either<Throwable, String> =
        Either.catch {
            storeWebClient.get()
                .uri("/$gameId")
                .retrieve()
                .bodyToMono<String>()
                .awaitSingle()
        }


    override suspend fun getAllGames(): Either<Throwable, GetAllSteamGamesResponse> {
        val url = "/ISteamApps/GetAppList/v0002/"

        logger.debug("Getting all games from steam")

        return Either.catch {
            apiWebClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono<GetAllSteamGamesResponse>()
                .awaitFirst()
        }.also {
            logger.debug("Getting all games from steam is success")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }
}
