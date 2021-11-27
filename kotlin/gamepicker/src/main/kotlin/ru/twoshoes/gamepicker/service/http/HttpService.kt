package ru.twoshoes.gamepicker.service.http

import arrow.core.Either
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Service
class HttpService : IHttpService {

    private val webClient by lazy {
        WebClient.builder()
            .exchangeStrategies(
                ExchangeStrategies.builder()
                    .codecs { configurer ->
                        configurer.defaultCodecs().maxInMemorySize(maxSizeFile512mb)
                    }.build()
            )
            .build()
    }

    override suspend fun downloadFile(url: String): Either<Throwable, ByteArray> =
        Either.catch {
            val bytes = webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToMono(ByteArray::class.java)
                .awaitSingle()
            bytes
        }

    companion object {
        const val maxSizeFile512mb = 512 * 1024 * 1024
    }
}
