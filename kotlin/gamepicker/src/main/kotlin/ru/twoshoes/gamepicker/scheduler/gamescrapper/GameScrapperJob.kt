package ru.twoshoes.gamepicker.scheduler.gamescrapper

import arrow.core.getOrHandle
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component
import ru.twoshoes.gamepicker.service.ISteamService

@Component
class GameScrapperJob(
    private val steamService: ISteamService
) : QuartzJobBean() {

    override fun executeInternal(context: JobExecutionContext) {
        logger.info("Game scrapping is start")

        runBlocking {
            run()
        }

        logger.info("Game scrapping is finish")
    }

    @OptIn(InternalCoroutinesApi::class)
    private suspend fun run() {
        val games = steamService.getAllGames().getOrHandle { error ->
            logger.error("Can not scrap all games: ${error.message}")

            return
        }

        logger.debug("Count of games = ${games.applist.apps.size}")

//        games.applist.apps.asFlow().collect { game ->
//            val page = steamService.getHtmlPage(gameId = game.appid).getOrHandle { error ->
//                logger.warn("Can not get steam page by id: ${game.appid}")
//                return@collect
//            }
//
//            logger.debug("Scrap ")
//        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }
}
