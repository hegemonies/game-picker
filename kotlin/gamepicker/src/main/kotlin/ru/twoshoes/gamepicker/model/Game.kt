package ru.twoshoes.gamepicker.model

import ru.twoshoes.gamepicker.consts.TableName
import ru.twoshoes.gamepicker.model.genre.GenresGames
import ru.twoshoes.gamepicker.model.platform.PlatformsGames
import ru.twoshoes.gamepicker.model.tag.TagsGames
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = TableName.GAME_PICKER_GAMES)
data class Game(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    val id: Long = 0,

    val title: String,

    val description: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "developer_id")
    val developer: Developer,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id")
    val publisher: Publisher,

    @OneToMany(mappedBy = "game")
    val genres: List<GenresGames> = emptyList(),

    @OneToMany(mappedBy = "game")
    val platforms: List<PlatformsGames> = emptyList(),

    @OneToMany(mappedBy = "game")
    val tags: List<TagsGames> = emptyList()
)
