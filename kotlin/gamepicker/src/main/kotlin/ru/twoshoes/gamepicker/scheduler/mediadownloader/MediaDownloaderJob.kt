package ru.twoshoes.gamepicker.scheduler.mediadownloader

import arrow.core.getOrHandle
import kotlinx.coroutines.runBlocking
import org.quartz.JobExecutionContext
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.quartz.QuartzJobBean
import org.springframework.stereotype.Component
import ru.twoshoes.gamepicker.configuration.property.MinioProperties
import ru.twoshoes.gamepicker.consts.MediaType
import ru.twoshoes.gamepicker.repository.MediaLinkRepository
import ru.twoshoes.gamepicker.service.s3.IS3Service
import ru.twoshoes.gamepicker.service.http.IHttpService

@Component
class MediaDownloaderJob(
    private val httpService: IHttpService,
    private val mediaLinkRepository: MediaLinkRepository,
    private val s3Service: IS3Service,
    private val minioProperties: MinioProperties
) : QuartzJobBean() {

    override fun executeInternal(context: JobExecutionContext): Unit = runBlocking {
        logger.info("Start media downloader")

        runCatching {
            var pageNumber = 0

            do {
                val page = PageRequest.of(pageNumber++, 100)
                val mediaLinks = mediaLinkRepository.findAllByDownloaded(false, page).content

                mediaLinks.forEach { mediaLink ->
                    logger.debug("Downloading file from ${mediaLink.mediaLink}")

                    val byteArray = httpService.downloadFile(mediaLink.mediaLink).getOrHandle { error ->
                        logger.warn("Can not download file ${mediaLink.mediaLink}: ${error.message}", error)
                        return@forEach
                    }

                    val mediaType = MediaType.valueOf(mediaLink.mediaType)
                        ?: throw Throwable("Not found such media type as ${mediaLink.mediaType}")

                    val fileName = mediaLink.mediaLink.replace(oldChar = '/', newChar = '\\')

                    logger.debug("Save file ${mediaLink.mediaLink} to S3 in bucket ${minioProperties.bucket}")

                    s3Service.saveToBucket(
                        bytes = byteArray,
                        bucketName = minioProperties.bucket,
                        contentType = mediaType.contentType,
                        fileName = fileName
                    ).getOrHandle { error ->
                        logger.warn("Can not save file $fileName to bucket ${minioProperties.bucket}: ${error.message}")
                        return@forEach
                    }

                    mediaLinkRepository.setDownloaded(mediaLink.id)
                }
            } while (mediaLinks.isNotEmpty())
        }.onFailure { error ->
            logger.error("Can not download media files: ${error.message}", error)
        }

        logger.info("Finish media downloader")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }
}
