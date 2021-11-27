package ru.twoshoes.gamepicker.scheduler.gamescrapper

import org.quartz.CalendarIntervalScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.SimpleScheduleBuilder
import org.quartz.TriggerBuilder
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
            .startNow()
            .build()

        schedulerFactory.scheduler.scheduleJob(job, setOf(trigger), true)
    }
}