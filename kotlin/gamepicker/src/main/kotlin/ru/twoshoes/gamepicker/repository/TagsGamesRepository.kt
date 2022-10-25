package ru.twoshoes.gamepicker.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import ru.twoshoes.gamepicker.model.tag.TagsGames
import ru.twoshoes.gamepicker.model.tag.TagsGamesKey
import javax.transaction.Transactional

interface TagsGamesRepository : JpaRepository<TagsGames, TagsGamesKey> {

    @Modifying
    @Transactional
    @Query("FROM TagsGames tg WHERE tg.id.gameId = :gameId")
    fun findAllTagsByGameId(gameId: Long): List<TagsGames>
}
