package ru.twoshoes.gamepicker.model.platform

import ru.twoshoes.gamepicker.consts.TableName
import ru.twoshoes.gamepicker.model.Game
import javax.persistence.*

@Entity
@Table(name = TableName.GAME_PICKER_GAMES_PLATFORMS)
data class PlatformsGames(
    @EmbeddedId
    val id: PlatformsGamesKey,

    @ManyToOne
    @MapsId("platform_id")
    val platform: Platform,

    @ManyToOne
    @MapsId("game_id")
    val game: Game
)
