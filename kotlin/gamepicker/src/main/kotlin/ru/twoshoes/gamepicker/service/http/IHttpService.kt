package ru.twoshoes.gamepicker.service.http

import arrow.core.Either
import java.io.ByteArrayOutputStream

interface IHttpService {

    suspend fun downloadFile(url: String): Either<Throwable, ByteArrayOutputStream>
}
