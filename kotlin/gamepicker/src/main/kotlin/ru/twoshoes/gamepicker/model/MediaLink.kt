package ru.twoshoes.gamepicker.model

import org.hibernate.Hibernate
import ru.twoshoes.gamepicker.consts.TableName
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = TableName.GAME_PICKER_MEDIA_LINKS)
data class MediaLink(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    @Column(name = "media_link")
    val mediaLink: String,

    @Column(name = "game_id")
    val gameId: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as MediaLink

        return id == other.id
    }

    override fun hashCode(): Int = 0

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , mediaLink = $mediaLink , gameId = $gameId )"
    }
}
