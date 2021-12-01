package ru.twoshoes.gamepicker.service.http

import arrow.core.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.withContext
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlow
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.concurrent.atomic.AtomicLong

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

    suspend fun downloadFile2(url: String): Either<Throwable, OutputStream> {
        return Either.catch {
            val dataSteam = webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToFlow<DataBuffer>()

            val fileWriteOffset = AtomicLong(0)
            val outputSteam = ByteArrayOutputStream()

            outputSteam.use { byteArrayOutputStream ->
                dataSteam.collect { dataBuffer ->
                    val readableByteCount = dataBuffer.readableByteCount()
                    val destinationByteArray = ByteArray(readableByteCount)
                    dataBuffer.read(destinationByteArray)
                    fileWriteOffset.addAndGet(readableByteCount.toLong())
                    withContext(Dispatchers.IO) {
                        byteArrayOutputStream.write(destinationByteArray)
                    }
                }
            }

            outputSteam
        }
    }

    companion object {
        const val maxSizeFile512mb = 512 * 1024 * 1024
    }
}
