package ru.twoshoes.gamepicker.service.http

import arrow.core.Either
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.io.ByteArrayOutputStream
import java.util.concurrent.atomic.AtomicLong

@Service
class HttpService : IHttpService {

    private val webClient by lazy {
        WebClient.create()
    }

    override suspend fun downloadFile(url: String): Either<Throwable, ByteArrayOutputStream> {
        return Either.catch {
            val dataSteam = webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToFlux(DataBuffer::class.java)

            val fileWriteOffset = AtomicLong(0)
            val outputSteam = ByteArrayOutputStream()

            outputSteam.use { byteArrayOutputStream ->
                dataSteam.subscribe { dataBuffer ->
                    val readableByteCount = dataBuffer.readableByteCount()
                    val destinationByteArray = ByteArray(readableByteCount)
                    dataBuffer.read(destinationByteArray)
                    fileWriteOffset.addAndGet(readableByteCount.toLong())
                    byteArrayOutputStream.write(destinationByteArray)
                }
            }

            outputSteam
        }
    }
}
