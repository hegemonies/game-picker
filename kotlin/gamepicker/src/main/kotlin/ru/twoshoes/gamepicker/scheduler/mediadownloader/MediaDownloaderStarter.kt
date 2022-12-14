package ru.twoshoes.gamepicker.scheduler.mediadownloader

import org.quartz.CalendarIntervalScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.TriggerBuilder
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.stereotype.Component
import ru.twoshoes.gamepicker.configuration.property.MediaDownloaderProperty

@Component
class MediaDownloaderStarter(
    private val schedulerFactory: SchedulerFactoryBean,
    private val mediaDownloaderProperty: MediaDownloaderProperty
) {

    @EventListener(ApplicationReadyEvent::class)
    fun createJob() {
        runCatching {
            if (!mediaDownloaderProperty.enable) {
                logger.info("Media downloader is not enable")
                return
            }

            val identity = "media-downloader"

            val job = JobBuilder.newJob(MediaDownloaderJob::class.java)
                .storeDurably()
                .withIdentity(identity)
                .build()

            val trigger = TriggerBuilder.newTrigger()
                .withSchedule(
                    CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
                        .withIntervalInMinutes(mediaDownloaderProperty.interval.toMinutes().toInt())
                )
                .build()

            schedulerFactory.scheduler.scheduleJob(job, setOf(trigger), true)
        }.onFailure { error ->
            logger.error("Can not create media downloader job: ${error.message}", error)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }
}
