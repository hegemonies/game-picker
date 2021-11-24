package ru.twoshoes.gamepicker.init

import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component
import ru.twoshoes.gamepicker.configuration.property.MinioProperties
import kotlin.system.measureTimeMillis

@Component
class MinioInit(
    private val minioClient: MinioClient,
    private val minioProperties: MinioProperties
) : InitializingBean {

    override fun afterPropertiesSet() {
        measureTimeMillis {
            logger.info("Start initialization of minio")

            runCatching {
                val isBucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                        .bucket(minioProperties.bucket)
                        .build()
                )

                if (!isBucketExists) {
                    logger.info("Create bucket ${minioProperties.bucket}")
                    minioClient.makeBucket(
                        MakeBucketArgs.builder()
                            .bucket(minioProperties.bucket)
                            .build()
                    )
                } else {
                    logger.info("Bucket ${minioProperties.bucket} is exists")
                }
            }.onFailure { error ->
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
