package ru.twoshoes.gamepicker.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.twoshoes.gamepicker.service.pageparsing.IPageParsingService
import ru.twoshoes.gamepicker.service.pageparsing.SteamPageParsingService

@Configuration
class PageParsingConfiguration {

    @Bean(name = ["steamPageParsingService"])
    fun steamPageParsingService(): IPageParsingService =
        SteamPageParsingService()
}
