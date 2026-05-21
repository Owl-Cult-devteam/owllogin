package io.owlcult.dev.login;

import com.mojang.logging.LogUtils;
import io.owlcult.dev.login.command.LoginCommand;
import io.owlcult.dev.login.command.RegisterCommand;
import io.owlcult.dev.login.model.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(OwlLogin.MODID)
public class OwlLogin {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "owllogin";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public OwlLogin(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (OwlLogin) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("COMMON SETUP");
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("SERVER STARTING");

        DatabaseManager.init_connection(); // Initialize the database connection one time per start
        DatabaseManager.init(); // Initialize the database table

        LOGGER.info("Database connection initialized");

        AuthController.init();
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        LoginCommand.register(event.getDispatcher());
        RegisterCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            LOGGER.info("Подключение игрока " + player.getGameProfile().getName() + " успешно перехвачено.");

            Player pm = new Player();

            pm.nickname = player.getScoreboardName();
            pm.gameMode = player.gameMode;

            player.setGameMode(GameType.ADVENTURE);
            player.setTabListHeader(Component.literal("Not logged in"));

            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, -1, 255, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, -1, 255, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 255, false, false));

            AuthController.player_connected(player.getScoreboardName(), player, pm);
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AuthController.player_disconnected(player.getScoreboardName());
        }
    }
}
