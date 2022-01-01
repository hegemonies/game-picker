package ru.twoshoes.gamepicker.consts

enum class MarketName(val value: Int) {
    STEAM(0);

    companion object {
        fun valueOf(value: Int) =
            values().find { it.value == value }
    }
}
