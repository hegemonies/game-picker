package ru.twoshoes.gamepicker.model.tag

import org.hibernate.Hibernate
import ru.twoshoes.gamepicker.consts.TableName
import javax.persistence.*

@Entity
@Table(name = TableName.GAME_PICKER_TAGS)
data class Tag(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    val name: String,

    @OneToMany(mappedBy = "tag")
    val games: List<TagsGames> = emptyList()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Tag

        return id == other.id
    }

    override fun hashCode(): Int = 0

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name )"
    }
}
