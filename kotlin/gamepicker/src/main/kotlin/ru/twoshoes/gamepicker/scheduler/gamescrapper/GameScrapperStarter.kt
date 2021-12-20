package ru.twoshoes.gamepicker.scheduler.gamescrapper

import org.quartz.CalendarIntervalScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.TriggerBuilder
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.stereotype.Component
import ru.twoshoes.gamepicker.configuration.property.GameScrapperProperty

@Component
class GameScrapperStarter(
    private val schedulerFactory: SchedulerFactoryBean,
    private val gameScrapperProperty: GameScrapperProperty
) {

    @EventListener(ApplicationReadyEvent::class)
    fun start() {
        runCatching {

            if (!gameScrapperProperty.enable) {
                logger.info("Game scrapper is not enable")
                return
            }

            val identity = "game-scrapper-job"

            val job = JobBuilder.newJob(GameScrapperJob::class.java)
                .storeDurably()
                .withIdentity(identity)
                .build()

            val trigger = TriggerBuilder.newTrigger()
                .withSchedule(
                    CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
                        .withIntervalInMinutes(gameScrapperProperty.interval.toMinutes().toInt())
                )
                .build()

            schedulerFactory.scheduler.scheduleJob(job, setOf(trigger), true)
        }.onFailure { error ->
            logger.error("Can not schedule of game scrapper job: ${error.message}", error)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }
}
