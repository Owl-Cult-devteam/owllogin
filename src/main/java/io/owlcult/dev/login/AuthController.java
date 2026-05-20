package io.owlcult.dev.login;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.logging.LogUtils;
import io.owlcult.dev.login.model.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import org.slf4j.Logger;

public class AuthController {
    public static List<Tuple<Player, AuthState>> states;
    static Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        states = new ArrayList<>();
    }

    public static void player_connected(String nickname, ServerPlayer player) {
        DatabaseManager dbman = new DatabaseManager();

        LOGGER.info("Request to connect player " + nickname);

        Player p = dbman.get(nickname);

        if (p.password_hash.isEmpty()) {
            states.add(new Tuple<>(p, AuthState.NOT_REGISTERED));
            player.sendSystemMessage(Component.literal("Need to register, type /register <password>"));
            player.setTabListHeader(Component.literal("Need to register"));
            LOGGER.info("Player needs to register");
        } else {
            states.add(new Tuple<>(p, AuthState.NOT_LOGGED_IN));
            player.sendSystemMessage(Component.literal("Need to login, type /login <password>"));
            player.setTabListHeader(Component.literal("Need to login"));
            LOGGER.info("Player needs to login");
        }
    }

    public static void player_disconnected(String nickname, ServerPlayer player) {
        states = states.stream().filter(tuple -> !tuple.getA().nickname.equals(nickname)).collect(Collectors.toList());
    }

    public static AuthState get_player_state(String nickname) {
        List<Tuple<Player, AuthState>> t = states
                .stream()
                .filter(tuple -> tuple.getA().nickname.equals(nickname))
                .toList();

        if (t.size() != 1) {
            throw new RuntimeException("Impossible state");
        }

        return t.getFirst().getB();
    }

    public static void set_player_state(Player p, AuthState state) {
        for (int i = 0; i < states.size(); ++i) {
            Tuple<Player, AuthState> t = states.get(i);

            if (t.getA().nickname.equals(p.nickname))
                states.set(i, new Tuple<>(p, state));
        }
    }
}
