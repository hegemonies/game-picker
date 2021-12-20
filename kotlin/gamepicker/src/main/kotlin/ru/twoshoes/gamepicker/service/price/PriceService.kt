package ru.twoshoes.gamepicker.service.price

import arrow.core.Either
import org.springframework.stereotype.Service
import ru.twoshoes.gamepicker.consts.MarketName
import ru.twoshoes.gamepicker.model.Price
import ru.twoshoes.gamepicker.repository.PriceRepository

@Service
class PriceService(
    private val priceRepository: PriceRepository
) : IPriceService {

    override fun saveSteamPrice(price: Double, gameId: Long): Either<Throwable, Price> {
        return Either.catch {
            priceRepository.save(
                Price(
                    gameId = gameId,
                    marketName = MarketName.STEAM.value,
                    price = price
                )
            )
        }
    }
}
