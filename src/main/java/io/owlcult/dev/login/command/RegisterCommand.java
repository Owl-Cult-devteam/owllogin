package io.owlcult.dev.login.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
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
                    DatabaseManager man = new DatabaseManager();

                    // context.getSource().sendSystemMessage(
                    //     Component.literal(
                    //         "Введите /register <password> (еще раз?) для подтверждения пароля" // TODO: Михкуст, блять, че за хуйню ты сделал тут я не пойму
                    //     )
                    // );

                    if (
                        context.getSource().getPlayer() == null // If request not from player
                    ) return 0; // Exit

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
