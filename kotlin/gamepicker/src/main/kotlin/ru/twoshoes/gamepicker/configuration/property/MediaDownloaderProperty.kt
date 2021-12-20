package ru.twoshoes.gamepicker.configuration.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import java.time.Duration

@ConstructorBinding
@Validated
@ConfigurationProperties(prefix = "media-downloader")
class MediaDownloaderProperty(

    val interval: Duration,

    val enable: Boolean
)
