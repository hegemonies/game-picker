package ru.twoshoes.gamepicker.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.twoshoes.gamepicker.model.Developer

interface DeveloperRepository : JpaRepository<Developer, Long> {

    fun findByName(name: String): Developer?
}
