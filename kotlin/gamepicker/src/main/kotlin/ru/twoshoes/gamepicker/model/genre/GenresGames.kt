package ru.twoshoes.gamepicker.model.genre

import org.hibernate.Hibernate
import ru.twoshoes.gamepicker.consts.TableName
import ru.twoshoes.gamepicker.model.Game
import java.util.*
import javax.persistence.*

@Entity
@Table(name = TableName.GAME_PICKER_GAMES_GENRES)
data class GenresGames(
    @EmbeddedId
    val id: GenresGamesKey,

    @ManyToOne
    @MapsId("genre_id")
    val genre: Genre,

    @ManyToOne
    @MapsId("game_id")
    val game: Game
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as GenresGames

        return id == other.id
    }

    override fun hashCode(): Int = Objects.hash(id)

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(EmbeddedId = $id )"
    }
}
