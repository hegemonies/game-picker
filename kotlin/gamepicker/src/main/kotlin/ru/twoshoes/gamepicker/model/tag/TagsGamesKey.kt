package ru.twoshoes.gamepicker.model.tag

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class TagsGamesKey(
    @Column(name = "tag_id")
    val tagId: Long,

    @Column(name = "game_id")
    val gameId: Long
) : Serializable
