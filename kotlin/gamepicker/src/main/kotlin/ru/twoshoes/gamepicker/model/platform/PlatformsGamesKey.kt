package ru.twoshoes.gamepicker.model.platform

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class PlatformsGamesKey(
    @Column(name = "platform_id")
    val platformId: Long,

    @Column(name = "game_id")
    val gameId: Long
): Serializable
