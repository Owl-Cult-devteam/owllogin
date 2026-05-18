package io.owlcult.dev.login.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class RegisterCommand {

    public static void register(
        CommandDispatcher<CommandSourceStack> dispatcher
    ) {
        dispatcher.register(
            Commands.literal("register")
                .then(
                    Commands.argument("password", StringArgumentType.string())
                )
                .executes(context -> {
                    context
                        .getSource()
                        .sendSystemMessage(
                            Component.literal(
                                "Введите /register <password> для подтверждения пароля"
                            )
                        );

                    return 1;
                })
        );
    }
}
