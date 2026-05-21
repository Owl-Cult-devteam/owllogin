package io.owlcult.dev.login;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.logging.LogUtils;
import io.owlcult.dev.login.model.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.antlr.v4.runtime.misc.Triple;
import org.slf4j.Logger;

public class AuthController {
    public static List<Triple<Player, AuthState, ServerPlayerGameMode>> states;
    static Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        states = new ArrayList<>();
    }

    public static void player_connected(String nickname, ServerPlayer player, Player pm) {
        DatabaseManager dbman = new DatabaseManager();

        LOGGER.info("Request to connect player " + nickname);

        pm.password_hash = dbman.get(nickname).password_hash;

        if (pm.password_hash.isEmpty()) {
            states.add(new Triple<>(pm, AuthState.NOT_REGISTERED, pm.gameMode));
            player.sendSystemMessage(Component.literal("Need to register, type /register <password>"));
            player.setTabListHeader(Component.literal("Need to register"));
            LOGGER.info("Player needs to register");
        } else {
            states.add(new Triple<>(pm, AuthState.NOT_LOGGED_IN, pm.gameMode));
            player.sendSystemMessage(Component.literal("Need to login, type /login <password>"));
            player.setTabListHeader(Component.literal("Need to login"));
            LOGGER.info("Player needs to login");
        }
    }

    public static void player_disconnected(String nickname, ServerPlayer player) {
        states = states.stream().filter(triple-> !triple.a.nickname.equals(nickname)).collect(Collectors.toList());
    }

    public static AuthState get_player_state(String nickname) {
        List<Triple<Player, AuthState, ServerPlayerGameMode>> t = states
                .stream()
                .filter(triple -> triple.a.nickname.equals(nickname))
                .toList();

        if (t.size() != 1) {
            throw new RuntimeException("Impossible state");
        }

        return t.getFirst().b;
    }

    public static ServerPlayerGameMode get_player_gamemode(String nickname) {
        List<Triple<Player, AuthState, ServerPlayerGameMode>> t = states
                .stream()
                .filter(triple -> triple.a.nickname.equals(nickname))
                .toList();

        if (t.size() != 1)
            throw new RuntimeException("Impossible state");

        return t.getFirst().c;
    }

    public static void set_player_state(Player p, AuthState state, ServerPlayerGameMode gm) {
        for (int i = 0; i < states.size(); ++i) {
            Triple<Player, AuthState, ServerPlayerGameMode> t = states.get(i);

            if (t.a.nickname.equals(p.nickname))
                states.set(i, new Triple<>(p, state, gm));
        }
    }
}
