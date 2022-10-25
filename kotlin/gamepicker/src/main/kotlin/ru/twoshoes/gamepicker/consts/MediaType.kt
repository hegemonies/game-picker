package ru.twoshoes.gamepicker.consts

enum class MediaType(val number: Int, val contentType: String) {
    IMAGE(0, "image/jpeg"), VIDEO(1, "video/webm");

    companion object {
        fun valueOf(number: Int) =
            values().find { it.number == number }
    }
}
