package ru.twoshoes.gamepicker.scheduler.s3

import arrow.core.Either
import io.minio.messages.Item

interface IS3Service {

    fun saveToBucket(
        bytes: ByteArray,
        bucketName: String,
        contentType: String,
        fileName: String
    ): Either<Throwable, Unit>

    fun createBucketIfNotExists(bucketName: String): Either<Throwable, Unit>

    fun deleteBucket(bucketName: String): Either<Throwable, Unit>

    fun deleteAllFilesInBucket(bucketName: String): Either<Throwable, Unit>

    fun getObjectsFromBucket(bucketName: String): Either<Throwable, List<Item>>

    fun isBucketExists(bucketName: String): Either<Throwable, Boolean>
}
