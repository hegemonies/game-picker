package ru.twoshoes.gamepicker.configuration.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotEmpty

@ConstructorBinding
@Validated
@ConfigurationProperties(prefix = "steam")
data class SteamProperty(

    @field:NotEmpty
    val baseUrl: String
)
