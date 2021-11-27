package ru.twoshoes.gamepicker.scheduler.s3

import arrow.core.Either
import io.minio.*
import io.minio.messages.DeleteObject
import io.minio.messages.Item
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream

@Service
class S3Service(
    private val minioClient: MinioClient
) : IS3Service {

    override fun saveToBucket(
        bytes: ByteArray,
        bucketName: String,
        contentType: String,
        fileName: String
    ): Either<Throwable, Unit> =
        Either.catch {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .contentType(contentType)
                    .`object`(fileName)
                    .stream(ByteArrayInputStream(bytes), bytes.size.toLong(), -1)
                    .build()
            )
        }

    override fun createBucketIfNotExists(bucketName: String): Either<Throwable, Unit> =
        Either.catch {
            val isBucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build()
            )

            if (!isBucketExists) {
                logger.info("Create bucket $bucketName")

                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build()
                )
            } else {
                logger.info("Bucket $bucketName is exists")
            }
        }

    override fun deleteBucket(bucketName: String): Either<Throwable, Unit> =
        Either.catch {
            if (isBucketExistsInternal(bucketName)) {
                minioClient.removeBucket(
                    RemoveBucketArgs.builder()
                        .bucket(bucketName)
                        .build()
                )
            }
        }

    override fun deleteAllFilesInBucket(bucketName: String): Either<Throwable, Unit> =
        Either.catch {
            if (isBucketExistsInternal(bucketName)) {
                minioClient.removeObjects(
                    RemoveObjectsArgs.builder()
                        .bucket(bucketName)
                        .objects(
                            getObjectsFromBucketInternal(bucketName).map { item ->
                                DeleteObject(item.objectName().toString())
                            }
                        )
                        .build()
                )
            }
        }

    override fun getObjectsFromBucket(bucketName: String): Either<Throwable, List<Item>> =
        Either.catch {
            getObjectsFromBucketInternal(bucketName)
        }

    private fun getObjectsFromBucketInternal(bucketName: String): List<Item> =
        minioClient.listObjects(
            ListObjectsArgs.builder()
                .bucket(bucketName)
                .build()
        ).toList().map { it.get() }

    override fun isBucketExists(bucketName: String): Either<Throwable, Boolean> =
        Either.catch {
            isBucketExistsInternal(bucketName)
        }

    private fun isBucketExistsInternal(bucketName: String): Boolean =
        minioClient.bucketExists(
            BucketExistsArgs.builder()
                .bucket(bucketName)
                .build()
        )

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java.declaringClass)
    }
}
