package ru.twoshoes.gamepicker.configuration

import io.minio.MinioClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.twoshoes.gamepicker.configuration.property.MinioProperties

@Configuration
class MinioConfiguration(
    private val minioProperties: MinioProperties
) {

    @Bean
    fun minioClient(): MinioClient =
        MinioClient.builder()
            .credentials(minioProperties.accessKey, minioProperties.secretKey)
            .endpoint(minioProperties.url)
            .build()
}
