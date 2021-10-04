package ru.gamepicker.gamepicker.model

import ru.gamepicker.gamepicker.consts.TableName
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
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
)
