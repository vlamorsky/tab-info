package me.vlamorsky.spongeplugin.tabinfo.listener;

import me.vlamorsky.spongeplugin.tabinfo.TabInfo;
import me.vlamorsky.spongeplugin.tabinfo.config.Config;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class PlayerJoinListener implements EventListener<ClientConnectionEvent.Join> {

    private final Config config;
    private final Logger logger;

    public PlayerJoinListener() {
        config = TabInfo.getInstance().getConfig();
        logger = TabInfo.getInstance().getLogger();
    }

    @Override
    public void handle(ClientConnectionEvent.Join event) throws Exception {

        Player player = event.getTargetEntity();

        player.getTabList().setHeaderAndFooter(
                TabInfo.getInstance().getHeaderText(),
                TabInfo.getInstance().getFooterText(player.getConnection().getLatency()));
    }
}
