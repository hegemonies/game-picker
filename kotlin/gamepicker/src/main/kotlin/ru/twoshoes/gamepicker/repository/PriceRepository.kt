package ru.twoshoes.gamepicker.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.twoshoes.gamepicker.model.Price

interface PriceRepository : JpaRepository<Price, Long> {

    fun findAllByGameId(gameId: Long): List<Price>
}
