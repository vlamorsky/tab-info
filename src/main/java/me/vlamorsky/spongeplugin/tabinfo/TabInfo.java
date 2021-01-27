package me.vlamorsky.spongeplugin.tabinfo;

import com.google.inject.Inject;
import me.vlamorsky.spongeplugin.tabinfo.command.Reload;
import me.vlamorsky.spongeplugin.tabinfo.config.Config;
import me.vlamorsky.spongeplugin.tabinfo.config.Permissions;
import me.vlamorsky.spongeplugin.tabinfo.listener.PlayerJoinListener;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.*;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.nio.file.Path;

@Plugin(
        id = "tab-info",
        name = "Tab Info",
        version = "1.0.1",
        description = "Tab Info plugin for Sponge")
public class TabInfo {

    private static TabInfo instance = null;
    public final static String VERSION = "1.0.1";
    public final static String NAME = "Tab Info";

    private Logger logger;
    private Game game;

    private Config config;
    private Path path;

    private Text headerText = Text.EMPTY;
    private Text footerText = Text.EMPTY;

    @Inject
    public TabInfo(Game game,
                   Logger logger_,
                   @DefaultConfig(sharedRoot = false) Path path) {

        TabInfo.instance = this;
        this.game = game;
        logger = logger_;
        this.path = path;
    }

    @Listener
    public void init(GameInitializationEvent event) {
        loadConfig();
        registerCommand();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        registerListeners();
        registerTasks();
    }

    private void registerListeners() {
        Sponge.getEventManager().registerListener(
                this, ClientConnectionEvent.Join.class, new PlayerJoinListener());
    }

    private void registerTasks() {
        Task.builder()
                .execute(new Runnable() {
                    @Override
                    public void run() {
                        updateTabInfo();
                    }
                })
        .intervalTicks(40)
        .async()
        .submit(this);
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

    private void registerCommand() {
        CommandSpec reload = CommandSpec.builder()
                .permission(Permissions.COMMAND_RELOAD)
                .executor(new Reload())
                .build();

        CommandSpec tab = CommandSpec.builder()
                .child(reload, "reload")
                .build();

        game.getCommandManager().register(this, tab, "tabinfo");
    }

    public synchronized void updateTabInfo() {

        headerText = textFromLegacy(config.HEADER_CONTENT);

        footerText = Text.of("\n");
        if (config.FOOTER_ONLINE_ENABLED) {
            footerText = footerText.concat(
                    textFromLegacy("  " + config.FOOTER_ONLINE_CONTENT + Sponge.getServer().getOnlinePlayers().size() + "  "));
        }

        if (config.FOOTER_TPS_ENABLED) {
            footerText = footerText.concat(
                    textFromLegacy("  " + config.FOOTER_TPS_CONTENT + Sponge.getServer().getTicksPerSecond() + "  "));
        }

        Sponge.getServer().getOnlinePlayers().forEach(player -> {
            player.getTabList()
                    .setHeaderAndFooter(
                            headerText,
                            getFooterText(player.getConnection().getLatency()));
        });
    }

    private Text textFromLegacy(String legacy) {
        return TextSerializers.FORMATTING_CODE.deserializeUnchecked(legacy);
    }

    public static TabInfo getInstance() {
        return instance;
    }

    public Text getHeaderText() {
        return headerText;
    }

    public Text getFooterText(int latency) {
        if (config.FOOTER_PING_ENABLED) {
            return footerText.concat(textFromLegacy("  " + config.FOOTER_PING_CONTENT + latency + config.FOOTER_PING_MS_CONTENT + "  "));
        }

        return footerText;
    }

    public Logger getLogger() {
        return logger;
    }

    public Config getConfig() {
        return config;
    }
}