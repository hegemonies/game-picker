package ru.twoshoes.gamepicker.service.pageparsing

import arrow.core.Either
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory

class SteamPageParsingService : IPageParsingService {

    override suspend fun parsePage(page: String): Either<Throwable, Document> =
        Either.catch {
            Jsoup.parse(page)
        }

    override suspend fun getTitle(document: Document): Either<Throwable, String> =
        Either.catch {
            document.title()
        }

    override suspend fun getPrice(document: Document): Either<Throwable, Long> {
        return Either.catch {
            document.body()
                .getElementsByClass("game_purchase_price price")
                .first()
                ?.attributes()
                ?.get("data-price-final")
                ?.toLong()
                ?.div(100)
                ?: run {
                    logger.warn("Can not find price")
                    0L
                }
        }
    }

    override suspend fun getMediaLinks(document: Document): Either<Throwable, List<String>> {
        return Either.catch {
            document.body()
                .getElementById("highlight_player_area")
                ?.siblingElements()
                ?.drop(1)
                ?.map { mediaElement ->
                    if (mediaElement.hasClass("highlight_player_item highlight_screenshot")) {
                        val linkPostfix = mediaElement.id().substringAfter("highlight_screenshot_")
                        "https://cdn.cloudflare.steamstatic.com/steam/apps/1196590/$linkPostfix"
                    } else if (mediaElement.hasClass("highlight_player_item highlight_movie")) {
                        mediaElement.attr("data-webm-source")
                    } else {
                        ""
                    }
                } ?: emptyList()
        }
    }

    override suspend fun getDeveloper(document: Document): Either<Throwable, String> {
        return Either.catch {
            document.body().getElementsByAttributeValueContaining("href", "developer").first()?.data()
                ?: "No developer"
        }
    }

    override suspend fun getPublisher(document: Document): Either<Throwable, String> {
        return Either.catch {
            document.body().getElementsByAttributeValueContaining("href", "publisher").first()?.data()
                ?: "No publisher"
        }
    }

    override suspend fun getDescription(document: Document): Either<Throwable, String> {
        return Either.catch {
            document.body().getElementById("game_area_description")?.data() ?: "No description"
        }
    }

    override suspend fun getReleaseDate(document: Document): Either<Throwable, String> {
        return Either.catch {
            document.body().getElementsByClass("date").first()?.data() ?: "No release date"
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }
}
