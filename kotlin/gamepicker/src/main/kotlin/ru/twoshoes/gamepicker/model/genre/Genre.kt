package ru.twoshoes.gamepicker.model.genre

import org.hibernate.Hibernate
import ru.twoshoes.gamepicker.consts.TableName.GAME_PICKER_GENRES
import javax.persistence.*

@Entity
@Table(name = GAME_PICKER_GENRES)
data class Genre(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    val name: String,

    @OneToMany(mappedBy = "genre")
    val games: List<GenresGames> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Genre

        return id == other.id
    }

    override fun hashCode(): Int = 0

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name )"
    }
}
