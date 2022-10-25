package ru.twoshoes.gamepicker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class GamepickerApplication

fun main(args: Array<String>) {
    runApplication<GamepickerApplication>(*args)
}
