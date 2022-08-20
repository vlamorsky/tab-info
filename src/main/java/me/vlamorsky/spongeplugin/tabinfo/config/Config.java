package me.vlamorsky.spongeplugin.tabinfo.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;
import java.nio.file.Path;

public class Config {

    private Path path;
    private ConfigurationLoader<CommentedConfigurationNode> loader;
    private CommentedConfigurationNode node;

    public String HEADER_CONTENT;

    public boolean FOOTER_ONLINE_ENABLED;
    public String FOOTER_ONLINE_CONTENT;

    public boolean FOOTER_TPS_ENABLED;
    public String FOOTER_TPS_CONTENT;

    public boolean FOOTER_PING_ENABLED;
    public String FOOTER_PING_CONTENT;
    public String FOOTER_PING_MS_CONTENT;

    public boolean FOOTER_WORLD_ENABLED;
    public String FOOTER_WORLD_CONTENT;

    public boolean FOOTER_CUSTOM_ENABLED;
    public String FOOTER_CUSTOM_CONTENT;

    public Config(Path path) throws IOException {

        this.path = path;
        loader = HoconConfigurationLoader.builder().path(path).build();

        loadConfiguration();
    }

    public synchronized void loadConfiguration() throws IOException {
        node = loader.load();

        HEADER_CONTENT = check(
                node.node("header", "content"),
                "§l§aTest Server\n",
                "Header content with minecraft formatting codes")
                .getString();




        FOOTER_ONLINE_ENABLED = check(
                node.node("footer", "online-enabled"),
                true,
                "[true/false]")
                .getBoolean();

        FOOTER_ONLINE_CONTENT = check(
                node.node("footer", "online-content"),
                "§7online: §6",
                "Footer, 'online' content")
                .getString();




        FOOTER_TPS_ENABLED = check(
                node.node("footer", "tps-enabled"),
                true)
                .getBoolean();

        FOOTER_TPS_CONTENT = check(
                node.node("footer", "tps-content"),
                "§7tps: §6",
                "Footer, 'tps' content")
                .getString();




        FOOTER_PING_ENABLED = check(
                node.node("footer", "ping-enabled"),
                true)
                .getBoolean();

        FOOTER_PING_CONTENT = check(
                node.node("footer", "ping-content"),
                "§7ping: §2",
                "Footer, 'ping' content")
                .getString();

        FOOTER_PING_MS_CONTENT = check(
                node.node("footer", "ping-ms-content"),
                "§7ms",
                "Footer, ping 'ms' content")
                .getString();




        FOOTER_WORLD_ENABLED = check(
                node.node("footer", "world-enabled"),
                false)
                .getBoolean();

        FOOTER_WORLD_CONTENT = check(
                node.node("footer", "world-content"),
                "\n§7world name: §6",
                "Footer, 'world' content, \"\\n\" - for new tab line (works in other settings too)")
                .getString();




        FOOTER_CUSTOM_ENABLED = check(
                node.node("footer", "custom-enabled"),
                false)
                .getBoolean();

        FOOTER_CUSTOM_CONTENT = check(
                node.node("footer", "custom-content"),
                "\n\n§awww§2.§atestserver§2.§acom",
                "Footer, custom content")
                .getString();




        loader.save(node);
    }

    private CommentedConfigurationNode check(CommentedConfigurationNode node, Object defaultValue, String comment) throws SerializationException {
        if (node.virtual()) {
            node.set(defaultValue).comment(comment);
        }
        return node;
    }

    private CommentedConfigurationNode check(CommentedConfigurationNode node, Object defaultValue) throws SerializationException {
        if (node.virtual()) {
            node.set(defaultValue);
        }
        return node;
    }
}
