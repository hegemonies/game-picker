package ru.twoshoes.gamepicker.configuration.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "minio")
@ConstructorBinding
class MinioProperties(
    val accessKey: String,
    val secretKey: String,
    val url: String,
    val bucket: String
)
