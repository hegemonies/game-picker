package ru.twoshoes.gamepicker.scheduler.gamescrapper

import arrow.core.Either
import arrow.core.getOrHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component
import ru.twoshoes.gamepicker.model.Developer
import ru.twoshoes.gamepicker.model.Game
import ru.twoshoes.gamepicker.model.Publisher
import ru.twoshoes.gamepicker.repository.DeveloperRepository
import ru.twoshoes.gamepicker.repository.GameRepository
import ru.twoshoes.gamepicker.repository.PublisherRepository
import ru.twoshoes.gamepicker.service.medialink.IMediaLinkService
import ru.twoshoes.gamepicker.service.pageparsing.IPageParsingService
import ru.twoshoes.gamepicker.service.price.IPriceService
import ru.twoshoes.gamepicker.service.steam.ISteamService
import kotlin.system.measureTimeMillis

@Component
class GameScrapperJob(
    private val steamService: ISteamService,
    @Qualifier(value = "steamPageParsingService")
    private val steamPageParsingService: IPageParsingService,
    @Qualifier(value = "gameScrapperCoroutineScope")
    private val gameScrapperCoroutineScope: CoroutineScope,
    private val gameRepository: GameRepository,
    private val developerRepository: DeveloperRepository,
    private val publisherRepository: PublisherRepository,
    private val priceService: IPriceService,
    private val mediaLinksService: IMediaLinkService
) : QuartzJobBean() {

    override fun executeInternal(context: JobExecutionContext) {
        logger.info("Game scrapping is start")

        runBlocking {
            measureTimeMillis {
                parseSteamGames()
            }.also { elapsed ->
                logger.info("Scrapping of steam games took $elapsed ms")
            }
        }

        logger.info("Game scrapping is finish")
    }

    private suspend fun parseSteamGames() {
        val games = steamService.getAllGames().getOrHandle { error ->
            logger.error("Can not scrap all games: ${error.message}")

            return
        }

        logger.debug("Count of games = ${games.applist.apps.size}")

        games.applist.apps.asFlow().collect { game ->
//            gameScrapperCoroutineScope.async {
//                delay(game.appid % 1000)

            logger.debug("Scrapping of the game '${game.name}' ${game.appid}")

            val page = steamService.getHtmlPage(gameId = game.appid).getOrHandle { error ->
                logger.warn("Can not get steam page by id: ${game.appid}: ${error.message}")
                return@collect
            }

            val document = steamPageParsingService.parsePage(page).getOrHandle { error ->
                logger.warn("Can not parse html page of game ${game.appid}, ${game.name}: ${error.message}")
                return@collect
            }

            val title = steamPageParsingService.getTitle(document).getOrHandle { error ->
                logger.warn("Can not get title for steam game by id #${game.appid}: ${error.message}")
                return@collect
            }.let { title ->
                if (title.contains("Welcome to Steam")) {
                    logger.warn("Game page for game ${game.appid}, ${game.name} not contains need game name $title")
                    return@collect
                }

                title.dropLast(9)
            }

            val developer = steamPageParsingService.getDeveloper(document).getOrHandle { error ->
                logger.warn("Can not find developer on page of game ${game.appid}, ${game.name}: ${error.message}")
                return@collect
            }

            val publisher = steamPageParsingService.getPublisher(document).getOrHandle { error ->
                logger.warn("Can not find publisher on page of game ${game.appid}, ${game.name}: ${error.message}")
                return@collect
            }

            val description = steamPageParsingService.getDescription(document).getOrHandle { error ->
                logger.warn("Can not find description on page of game ${game.appid}, ${game.name}: ${error.message}")
                return@collect
            }

            val releaseDate = steamPageParsingService.getReleaseDate(document).getOrHandle { error ->
                logger.warn("Can not find release date on page of game ${game.appid}, ${game.name}: ${error.message}")
                return@collect
            }

            // ...
            val gameModel = Either.catch {
                gameRepository.save(
                    Game(
                        title = title,
                        steamAppId = game.appid,
                        description = description,
                        developer = getDeveloper(developer).getOrHandle { error ->
                            logger.warn("Can not get developer by name: $developer: ${error.message}")
                            return@collect
                        },
                        publisher = getPublisher(publisher).getOrHandle { error ->
                            logger.warn("Can not get publisher by name: $publisher: ${error.message}")
                            return@collect
                        },
                        releaseDate = releaseDate,
                    )
                )
            }.getOrHandle { error ->
                logger.error("Can not save game $game to database: ${error.message}")
                return@collect
            }

            val price = steamPageParsingService.getPrice(document).getOrHandle { error ->
                logger.warn("Can not find media link on page of game ${game.appid}, ${game.name}: ${error.message}")
                return@collect
            }

            priceService.saveSteamPrice(price = price / 100.0, gameId = gameModel.id).getOrHandle { error ->
                logger.warn("Can not find release date on page of game ${game.appid}, ${game.name}: ${error.message}")
                return@collect
            }

            val mediaLinks = steamPageParsingService.getMediaLinks(document, game.appid).getOrHandle { error ->
                logger.warn("Can not find media links on page of game ${game.appid}, ${game.name}: ${error.message}")
                return@collect
            }

            mediaLinksService.saveMediaLinks(
                mediaLinks = mediaLinks,
                gameId = gameModel.id
            ).getOrHandle { error ->
                logger.warn(
                    "Can not save media links on page of game ${game.appid}, ${game.name}: ${error.message}",
                    error
                )
                return@collect
            }

//            }
        }
    }

    private fun getDeveloper(name: String): Either<Throwable, Developer> {
        val developer = runCatching {
            developerRepository.findByName(name)
        }.getOrElse { error ->
            return Either.Left(error)
        }

        return if (developer == null) {
            Either.catch {
                developerRepository.save(
                    Developer(
                        name = name
                    )
                )
            }
        } else {
            Either.Right(developer)
        }
    }

    private fun getPublisher(name: String): Either<Throwable, Publisher> {
        val publisher = runCatching {
            publisherRepository.findByName(name)
        }.getOrElse { error ->
            return Either.Left(error)
        }

        return if (publisher == null) {
            Either.catch {
                publisherRepository.save(
                    Publisher(
                        name = name
                    )
                )
            }
        } else {
            Either.Right(publisher)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }
}
