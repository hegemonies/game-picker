package ru.twoshoes.gamepicker.service.http

import arrow.core.Either

interface IHttpService {

    suspend fun downloadFile(url: String): Either<Throwable, ByteArray>
}
