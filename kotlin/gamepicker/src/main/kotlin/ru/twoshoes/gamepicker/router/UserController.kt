package ru.twoshoes.gamepicker.router

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import ru.twoshoes.gamepicker.service.user.UserService

@RestController
class UserController(
    private val userService: UserService
) {

    @GetMapping("/users")
    suspend fun getUsers() {
        userService.getUsers()
    }

    @GetMapping("/users/{user_id}")
    suspend fun getUser(@PathVariable userId: Long) {
        userService.getUser(userId)
    }
}
