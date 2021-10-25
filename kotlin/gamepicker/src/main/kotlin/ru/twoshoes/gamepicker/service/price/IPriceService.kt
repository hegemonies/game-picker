package ru.twoshoes.gamepicker.service.price

import arrow.core.Either
import ru.twoshoes.gamepicker.model.Price

interface IPriceService {

    fun saveSteamPrice(price: Int, gameId: Long): Either<Throwable, Price>
}
