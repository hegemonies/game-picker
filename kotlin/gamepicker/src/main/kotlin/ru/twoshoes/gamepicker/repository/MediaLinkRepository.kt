package ru.twoshoes.gamepicker.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.twoshoes.gamepicker.model.MediaLink

interface MediaLinkRepository : JpaRepository<MediaLink, Long>
