package ru.twoshoes.gamepicker.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import ru.twoshoes.gamepicker.model.MediaLink
import javax.transaction.Transactional

interface MediaLinkRepository : JpaRepository<MediaLink, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE MediaLink SET downloaded = true WHERE id = :mediaLinkId")
    fun setDownloaded(mediaLinkId: Long): Int

    fun findAllByDownloaded(downloaded: Boolean, pageable: Pageable): Page<MediaLink>

    fun findAllByGameId(gameId: Long): List<MediaLink>
}
