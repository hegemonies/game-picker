package ru.twoshoes.gamepicker.configuration

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

@Configuration
class GameScrapperCoroutineContextConfiguration {

    @Bean(name = ["gameScrapperCoroutineScope"])
    fun gameScrapperCoroutineScope() =
        object : CoroutineScope {
            override val coroutineContext: CoroutineContext =
                Executors.newFixedThreadPool(
                    Runtime.getRuntime().availableProcessors()
                ).asCoroutineDispatcher()
        }
}
