package io.owlcult.dev.login;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.logging.LogUtils;
import io.owlcult.dev.login.model.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.GameType;
import org.slf4j.Logger;

public class AuthController {
    public static List<Tuple<Player, AuthState>> states;
    static Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        states = new ArrayList<>();
    }

    public static void player_connected(String nickname, ServerPlayer player, Player pm) {
        DatabaseManager dbman = new DatabaseManager();

        LOGGER.info("Request to connect player " + nickname);

        pm.password_hash = dbman.get(nickname).password_hash;

        LOGGER.info("player gamemode: " + pm.gameMode.getName());

        if (pm.password_hash.isEmpty()) {
            states.add(new Tuple<>(pm, AuthState.NOT_REGISTERED));
            player.sendSystemMessage(Component.literal("Need to register, type /register <password>"));
            player.setTabListHeader(Component.literal("Need to register"));
            LOGGER.info("Player needs to register");
        } else {
            states.add(new Tuple<>(pm, AuthState.NOT_LOGGED_IN));
            player.sendSystemMessage(Component.literal("Need to login, type /login <password>"));
            player.setTabListHeader(Component.literal("Need to login"));
            LOGGER.info("Player needs to login");
        }
    }

    public static void player_disconnected(String nickname, ServerPlayer sp) {
        sp.setGameMode(get_player_gamemode(nickname));
        states = states.stream().filter(triple-> !triple.getA().nickname.equals(nickname)).collect(Collectors.toList());
    }

    public static AuthState get_player_state(String nickname) {
        List<Tuple<Player, AuthState>> t = states
                .stream()
                .filter(triple -> triple.getA().nickname.equals(nickname))
                .toList();

        if (t.size() != 1)
            return null;

        return t.getFirst().getB();
    }

    public static GameType get_player_gamemode(String nickname) {
        List<Tuple<Player, AuthState>> t = states
                .stream()
                .filter(triple -> triple.getA().nickname.equals(nickname))
                .toList();

        if (t.size() != 1)
            return null;

        return t.getFirst().getA().gameMode;
    }

    public static void set_player_gamemode(String nickname, GameType mode) {
        LOGGER.info("Player changed its gamemode to " + mode.getName());

        for (int i = 0; i < states.size(); ++i) {
            Tuple<Player, AuthState> t = states.get(i);

            if (t.getA().nickname.equals(nickname)) {
                Player pm = t.getA();
                pm.gameMode = mode;

                states.set(i, new Tuple<>(pm, t.getB()));
            }
        }
    }

    public static void set_player_state(Player p, AuthState state) {
        for (int i = 0; i < states.size(); ++i) {
            Tuple<Player, AuthState> t = states.get(i);

            if (t.getA().nickname.equals(p.nickname))
                states.set(i, new Tuple<>(t.getA(), state));
        }
    }
}
