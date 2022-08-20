package me.vlamorsky.spongeplugin.tabinfo;

import com.google.inject.Inject;
import me.vlamorsky.spongeplugin.tabinfo.config.Config;
import me.vlamorsky.spongeplugin.tabinfo.listener.PlayerJoinListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartingEngineEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.io.IOException;
import java.nio.file.Path;

@Plugin("tab-info")
public class TabInfo {

    private static TabInfo instance = null;

    private final PluginContainer pluginContainer;

    private Logger logger;

    private Config config;
    private Path path;

    private TextComponent headerText;
    private TextComponent footerText;

    @Inject
    public TabInfo(final PluginContainer pluginContainer,
                   final Logger logger,
                   @DefaultConfig(sharedRoot = false) Path path) {

        instance = this;
        this.pluginContainer = pluginContainer;
        this.logger = logger;
        this.path = path;

        loadConfig();
    }

    @Listener
    public void onRegisterCommands(final RegisterCommandEvent<Command.Parameterized> event) {
    }

    @Listener
    public void onServerStarting(final StartingEngineEvent<Server> event) {
        registerListeners();
        registerTasks();
    }

    @Listener
    public void onRefreshGameEvent(RefreshGameEvent refreshGameEvent) {
        try {
            config.loadConfiguration();
            logger.info("Configurations reloaded successfully!");

        } catch (IOException e) {
            logger.warn("Error while reloading configurations!");

            e.printStackTrace();
        }

        updateTabInfo();
    }

    private void registerListeners() {
        Sponge.eventManager().registerListeners(pluginContainer, new PlayerJoinListener());
    }

    private void registerTasks() {
        Task task = Task.builder()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        updateTabInfo();
                    }
                })
                .interval(Ticks.of(40))
                .plugin(pluginContainer)
                .build();

        Sponge.asyncScheduler().submit(task);
    }

    private void loadConfig() {
        try {
            config = new Config(path);
            logger.info("Configurations reloaded successfully.");
        } catch (Exception e) {
            logger.warn("Error while reloading configurations.");
            e.printStackTrace();
        }
    }

    public void updateTabInfo() {

        headerText = textFromLegacy(config.HEADER_CONTENT);

        footerText = Component.text("\n");
        if (config.FOOTER_ONLINE_ENABLED) {
            footerText = footerText.append(
                    textFromLegacy("  " + config.FOOTER_ONLINE_CONTENT + Sponge.server().onlinePlayers().size() + "  "));
        }

        if (config.FOOTER_TPS_ENABLED) {
            footerText = footerText.append(
                    textFromLegacy("  " + config.FOOTER_TPS_CONTENT + Sponge.server().ticksPerSecond() + "  "));
        }

        Sponge.server().onlinePlayers().forEach(player -> {
            player.tabList()
                    .setHeaderAndFooter(
                            headerText,
                            getFooterTextForPlayer(player));
        });
    }

    private TextComponent textFromLegacy(String legacy) {
        return PlainTextComponentSerializer.plainText().deserialize(legacy);
    }

    public static TabInfo getInstance() {
        return instance;
    }

    public TextComponent getHeaderText() {
        return headerText;
    }

    public TextComponent getFooterTextForPlayer(ServerPlayer serverPlayer) {

        TextComponent newFooterText = footerText;
        if (config.FOOTER_PING_ENABLED) {
            newFooterText =  newFooterText.
                    append(textFromLegacy("  " + config.FOOTER_PING_CONTENT + serverPlayer.connection().latency() + config.FOOTER_PING_MS_CONTENT + "  "));
        }

        if (config.FOOTER_WORLD_ENABLED) {
            newFooterText = newFooterText.append(
                    textFromLegacy(config.FOOTER_WORLD_CONTENT + serverPlayer.worldBorder().toString()));
        }

        if (config.FOOTER_CUSTOM_ENABLED) {
            newFooterText = newFooterText.append(
                    textFromLegacy(config.FOOTER_CUSTOM_CONTENT));
        }

        return newFooterText;
    }
}