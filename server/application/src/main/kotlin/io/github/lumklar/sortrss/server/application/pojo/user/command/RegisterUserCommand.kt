package io.github.lumklar.sortrss.server.application.pojo.user.command

data class RegisterUserCommand(
    val username: String,
    val plainPassword: String
)
