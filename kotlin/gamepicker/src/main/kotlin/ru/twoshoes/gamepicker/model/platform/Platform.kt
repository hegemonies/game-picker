package ru.twoshoes.gamepicker.model.platform

import org.hibernate.Hibernate
import ru.twoshoes.gamepicker.consts.TableName
import javax.persistence.*

@Entity
@Table(name = TableName.GAME_PICKER_PLATFORMS)
data class Platform(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    val name: String,

    @OneToMany(mappedBy = "platform")
    val games: List<PlatformsGames> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Platform

        return id == other.id
    }

    override fun hashCode(): Int = 0

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name )"
    }
}
