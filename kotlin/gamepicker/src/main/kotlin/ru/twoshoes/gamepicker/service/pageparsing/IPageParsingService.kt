package ru.twoshoes.gamepicker.service.pageparsing

import arrow.core.Either
import org.jsoup.nodes.Document

interface IPageParsingService {

    suspend fun parsePage(page: String): Either<Throwable, Document>

    suspend fun getTitle(document: Document): Either<Throwable, String>

    suspend fun getPrice(document: Document): Either<Throwable, Long>

    suspend fun getMediaLinks(document: Document, appId: Long): Either<Throwable, List<String>>

    suspend fun getDeveloper(document: Document): Either<Throwable, String>

    suspend fun getPublisher(document: Document): Either<Throwable, String>

    suspend fun getDescription(document: Document): Either<Throwable, String>

    suspend fun getReleaseDate(document: Document): Either<Throwable, String>

//    suspend fun getGenres(document: Document): Either<Throwable, List<String>>

//    suspend fun getTags(document: Document): Either<Throwable, List<String>>

//    suspend fun getPlatforms(document: Document): Either<Throwable, List<String>>
}
