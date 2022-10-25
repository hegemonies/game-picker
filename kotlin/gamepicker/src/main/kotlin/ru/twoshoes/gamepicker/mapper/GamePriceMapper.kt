package ru.twoshoes.gamepicker.mapper

import ru.twoshoes.gamepicker.consts.MarketName
import ru.twoshoes.gamepicker.dto.PriceDto
import ru.twoshoes.gamepicker.model.Price

fun Price.toDto() =
    PriceDto(
        marketName = MarketName.valueOf(this.marketName)?.name ?: "",
        price = this.price
    )

fun List<Price>.toDto() =
    this.map { price ->
        price.toDto()
    }
