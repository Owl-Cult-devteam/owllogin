package io.owlcult.dev.login.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import io.owlcult.dev.login.AuthController
import io.owlcult.dev.login.AuthState
import io.owlcult.dev.login.DatabaseManager
import io.owlcult.dev.login.model.Player
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component

class ChangePasswordCommand {
    companion object {
        @JvmStatic
        fun register(
            dispatcher: CommandDispatcher<CommandSourceStack>
        ) {
            dispatcher.register(
                Commands.literal("change_password").then(
                    Commands.argument("new_password", StringArgumentType.string())
                        .executes {
                            context ->
                            if (context.source.player == null)
                                return@executes 0

                            val player = context.source.player!!

                            when (AuthController.get_player_state(player.scoreboardName)){
                                AuthState.NOT_REGISTERED -> {
                                    context.source.sendFailure(Component.literal("You're not registered"))

                                    return@executes 0
                                }

                                AuthState.NOT_LOGGED_IN -> {
                                    context.source.sendFailure(Component.literal("Login first"))

                                    return@executes 0
                                }

                                else -> {}
                            }

                            val dbman = DatabaseManager()

                            val pmod = Player()

                            pmod.nickname = player.scoreboardName
                            pmod.password_hash = Player.hashPassword(context.getArgument("new_password", String::class.java))

                            dbman.change(pmod)

                            context.source.sendSystemMessage(Component.literal("Password changed!"))

                            return@executes 1
                        }
                )
            )
        }
    }
}