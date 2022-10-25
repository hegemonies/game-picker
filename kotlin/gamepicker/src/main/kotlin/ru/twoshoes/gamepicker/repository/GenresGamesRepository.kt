package ru.twoshoes.gamepicker.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import ru.twoshoes.gamepicker.model.genre.GenresGames
import ru.twoshoes.gamepicker.model.genre.GenresGamesKey
import javax.transaction.Transactional

interface GenresGamesRepository : JpaRepository<GenresGames, GenresGamesKey> {

    @Modifying
    @Transactional
    @Query("FROM GenresGames gg WHERE gg.id.gameId = :gameId")
    fun findAllByGameId(gameId: Long): List<GenresGames>
}
