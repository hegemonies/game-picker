package ru.twoshoes.gamepicker.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import ru.twoshoes.gamepicker.model.platform.PlatformsGames
import ru.twoshoes.gamepicker.model.platform.PlatformsGamesKey
import javax.transaction.Transactional

interface PlatformsGamesRepository : JpaRepository<PlatformsGames, PlatformsGamesKey> {

    @Modifying
    @Transactional
    @Query("FROM PlatformsGames pg WHERE pg.id.gameId = :gameId")
    fun findAllByGameId(gameId: Long): List<PlatformsGames>
}
