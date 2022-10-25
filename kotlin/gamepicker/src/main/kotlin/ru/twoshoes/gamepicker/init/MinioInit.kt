package ru.twoshoes.gamepicker.init

import arrow.core.getOrHandle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component
import ru.twoshoes.gamepicker.configuration.property.MinioProperties
import ru.twoshoes.gamepicker.service.s3.S3Service
import kotlin.system.measureTimeMillis

@Component
class MinioInit(
    private val minioProperties: MinioProperties,
    private val s3Service: S3Service
) : InitializingBean {

    override fun afterPropertiesSet() {
        measureTimeMillis {
            logger.info("Start initialization of minio")

            s3Service.createBucketIfNotExists(minioProperties.bucket).getOrHandle { error ->
                logger.error("Can not init minio: ${error.message}", error)
                return
            }

            logger.info("Success initialization of minio")
        }.also { elapsed ->
            logger.info("Initialization of minio took $elapsed ms")
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }
}
