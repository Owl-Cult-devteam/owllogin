package io.owlcult.dev.login.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.owlcult.dev.login.AuthController;
import io.owlcult.dev.login.AuthState;
import io.owlcult.dev.login.DatabaseManager;
import io.owlcult.dev.login.model.Player;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class RegisterCommand {

    public static void register(
        CommandDispatcher<CommandSourceStack> dispatcher
    ) {
        dispatcher.register(
            Commands.literal("register").then(
                Commands.argument(
                    "password",
                    StringArgumentType.string()
                ).executes(context -> {
                    if (
                        context.getSource().getPlayer() == null // If request not from player
                    ) return 0; // Exit

                    if (AuthController.get_player_state(context.getSource().getPlayer().getScoreboardName()) != AuthState.NOT_REGISTERED) {
                        context.getSource().sendFailure(Component.literal("You already registered"));
                        return 0;
                    }

                    DatabaseManager man = new DatabaseManager();

                    Player p = new Player();

                    p.nickname = context
                        .getSource()
                        .getPlayer()
                        .getGameProfile()
                        .getName();

                    p.password_hash = context.getArgument(
                        "password",
                        String.class
                    );

                    man.push(p);

                    context.getSource().getPlayer().connection.disconnect(Component.literal("Registration successful. Please, reconnect"));

                    return 1;
                })
            )
        );

        dispatcher.register(
            Commands.literal("daite_parol").executes(context -> {
                if (context.getSource().getPlayer() == null) return 0;

                DatabaseManager dbman = new DatabaseManager();

                Player p = dbman.get(
                    context.getSource().getPlayer().getGameProfile().getName()
                );

                context
                    .getSource()
                    .sendSystemMessage(
                        Component.literal(
                            p.nickname +
                                ", vash parol, pozhaluista: " +
                                p.password_hash
                        )
                    );

                return 1;
            })
        );
    }
}
