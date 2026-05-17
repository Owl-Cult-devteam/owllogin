package io.owlcult.dev.login;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class loginCmd {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("register")
            .executes(
                context -> {
                    context.getSource().sendSystemMessage(Component.literal("Pisun"));
                    return 1;
                }
            )
        );
    }
    public static void login(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("login")
            .executes(
                context -> {
                    context.getSource().sendSystemMessage(Component.literal("Pisun F6 блин"));
                    return 1;
                }
            )
        );
    }
}
