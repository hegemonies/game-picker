package ru.twoshoes.gamepicker.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.twoshoes.gamepicker.model.Publisher

interface PublisherRepository : JpaRepository<Publisher, Long> {

    fun findByName(name: String): Publisher?
}
