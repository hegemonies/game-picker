package ru.twoshoes.gamepicker.scheduler.mediadownloader

import arrow.core.getOrHandle
import io.minio.MinioClient
import io.minio.PutObjectArgs
import kotlinx.coroutines.runBlocking
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component
import ru.twoshoes.gamepicker.configuration.property.MinioProperties
import ru.twoshoes.gamepicker.consts.MediaType
import ru.twoshoes.gamepicker.repository.MediaLinkRepository
import ru.twoshoes.gamepicker.service.http.IHttpService
import java.io.ByteArrayInputStream

@Component
class MediaDownloaderJob(
    private val httpService: IHttpService,
    private val mediaLinkRepository: MediaLinkRepository,
    private val minioClient: MinioClient,
    private val minioProperties: MinioProperties
) : QuartzJobBean() {

    override fun executeInternal(context: JobExecutionContext): Unit = runBlocking {
        runCatching {
            var pageNumber = 0

            do {
                val page = PageRequest.of(pageNumber++, 100)
                val mediaLinks = mediaLinkRepository.findAll(page).content

                mediaLinks.forEach { mediaLink ->
                    val fileSteam = httpService.downloadFile(mediaLink.mediaLink).getOrHandle { error ->
                        logger.warn("Can not download file ${mediaLink.mediaLink}: ${error.message}", error)
                        return@forEach
                    }

                    val mediaType = MediaType.valueOf(mediaLink.mediaType)
                        ?: throw Throwable("Not found such media type as ${mediaLink.mediaType}")

                    minioClient.putObject(
                        PutObjectArgs.builder()
                            .bucket(minioProperties.bucket)
                            .contentType(mediaType.contentType)
                            .`object`(mediaLink.mediaLink.replace(oldChar = '/', newChar = '\\'))
                            .stream(ByteArrayInputStream(fileSteam.toByteArray()), 0, -1)
                            .build()
                    )

                    mediaLinkRepository.setDownloaded(mediaLink.id)
                }
            } while (mediaLinks.isNotEmpty())
        }.onFailure { error ->
            logger.error("Can not download media files: ${error.message}", error)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }
}
