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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;

public class LoginCommand {

    public static void register(
        CommandDispatcher<CommandSourceStack> dispatcher
    ) {
        dispatcher.register(
            Commands.literal("login")
                .executes(context -> {
                    context
                        .getSource()
                        .sendFailure(
                            Component.literal("Используйте: /login <password>")
                        );
                    return 0;
                })
                .then(
                    Commands.argument(
                        "password",
                        StringArgumentType.string()
                    ).executes(context -> {
                        if (context.getSource().getPlayer() == null)
                            return 0;

                        if (AuthController.get_player_state(context.getSource().getPlayer().getScoreboardName()) != AuthState.NOT_LOGGED_IN) {
                            context.getSource().sendFailure(Component.literal("You already logged in"));
                            return 0;
                        }

                        DatabaseManager dbman = new DatabaseManager();

                        String password = StringArgumentType.getString(
                            context,
                            "password"
                        );

                        if (password.isBlank()) {
                            context
                                    .getSource()
                                    .sendFailure(
                                            Component.literal(
                                                    "Введите пароль /login <password>"
                                            )
                                    );
                            return 0;
                        } else {
                            Player p = dbman.get(context.getSource().getPlayer().getScoreboardName());

                            if (!p.hashPassword(password).equals(p.password_hash)) { 	
                                context.getSource().sendFailure(Component.literal("неправильно, попробуй еще раз"));
                            } else {
                                AuthController.set_player_state(p, AuthState.LOGGED_IN, p.gameMode);
                                ServerPlayer sp = context.getSource().getPlayer();

                                sp.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                                sp.removeEffect(MobEffects.BLINDNESS);
                                sp.removeEffect(MobEffects.DAMAGE_RESISTANCE);

                                sp.setGameMode(AuthController.get_player_gamemode(p.nickname).getGameModeForPlayer());

                                sp.setTabListHeader(Component.empty());
                            }
                        }

                        return 1;
                    })
                )
        );
    }
}
