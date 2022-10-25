package ru.twoshoes.gamepicker.model.genre

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class GenresGamesKey(
    @Column(name = "genre_id")
    val genreId: Long,

    @Column(name = "game_id")
    val gameId: Long
) : Serializable
