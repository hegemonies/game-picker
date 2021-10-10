package ru.twoshoes.gamepicker.model.tag

import org.hibernate.Hibernate
import ru.twoshoes.gamepicker.consts.TableName
import ru.twoshoes.gamepicker.model.Game
import java.util.Objects
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.persistence.Table

@Entity
@Table(name = TableName.GAME_PICKER_GAMES_TAGS)
data class TagsGames(
    @EmbeddedId
    val id: TagsGamesKey,

    @ManyToOne
    @MapsId("tag_id")
    val tag: Tag,

    @ManyToOne
    @MapsId("game_id")
    val game: Game
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TagsGames

        return id != null && id == other.id
    }

    override fun hashCode(): Int = Objects.hash(id);

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(EmbeddedId = $id )"
    }
}
