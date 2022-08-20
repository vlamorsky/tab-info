package me.vlamorsky.spongeplugin.tabinfo.listener;

import me.vlamorsky.spongeplugin.tabinfo.TabInfo;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class PlayerJoinListener {

    @Listener
    public void handle(ServerSideConnectionEvent.Join event) {
        event.player().tabList().setHeaderAndFooter(
                TabInfo.getInstance().getHeaderText(),
                TabInfo.getInstance().getFooterTextForPlayer(event.player()));
    }
}
