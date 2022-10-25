package ru.twoshoes.gamepicker.dto

data class GetAllSteamGamesResponse(
    val applist: GetAllSteamGamesAppList
)

data class GetAllSteamGamesAppList(
    val apps: List<GetAllSteamGamesApp>
)

data class GetAllSteamGamesApp(
    val appid: Long,
    val name: String
)
