package me.vlamorsky.spongeplugin.tabinfo.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

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

    public Config(Path path) throws IOException, ObjectMappingException {

        this.path = path;
        loader = HoconConfigurationLoader.builder().setPath(path).build();

        loadConfiguration();
    }

    public synchronized void loadConfiguration() throws IOException, ObjectMappingException {
        node = loader.load();

        HEADER_CONTENT = check(
                node.getNode("header", "content"),
                "&l&aTest Server\n",
                "Header content with minecraft formatting codes")
                .getString();




        FOOTER_ONLINE_ENABLED = check(
                node.getNode("footer", "online-enabled"),
                true,
                "[true/false]")
                .getBoolean();

        FOOTER_ONLINE_CONTENT = check(
                node.getNode("footer", "online-content"),
                "&7online: &6",
                "Footer, 'online' content")
                .getString();




        FOOTER_TPS_ENABLED = check(
                node.getNode("footer", "tps-enabled"),
                true)
                .getBoolean();

        FOOTER_TPS_CONTENT = check(
                node.getNode("footer", "tps-content"),
                "&7tps: &6",
                "Footer, 'tps' content")
                .getString();




        FOOTER_PING_ENABLED = check(
                node.getNode("footer", "ping-enabled"),
                true)
                .getBoolean();

        FOOTER_PING_CONTENT = check(
                node.getNode("footer", "ping-content"),
                "&7ping: &2",
                "Footer, 'ping' content")
                .getString();

        FOOTER_PING_MS_CONTENT = check(
                node.getNode("footer", "ping-ms-content"),
                "&7ms",
                "Footer, ping 'ms' content")
                .getString();




        FOOTER_WORLD_ENABLED = check(
                node.getNode("footer", "world-enabled"),
                false)
                .getBoolean();

        FOOTER_WORLD_CONTENT = check(
                node.getNode("footer", "world-content"),
                "\n&7world name: &6",
                "Footer, 'world' content, \"\\n\" - for new tab line (works in other settings too)")
                .getString();




        FOOTER_CUSTOM_ENABLED = check(
                node.getNode("footer", "custom-enabled"),
                false)
                .getBoolean();

        FOOTER_CUSTOM_CONTENT = check(
                node.getNode("footer", "custom-content"),
                "\n\n&awww&2.&atestserver&2.&acom",
                "Footer, custom content")
                .getString();




        loader.save(node);
    }

    private CommentedConfigurationNode check(CommentedConfigurationNode node, Object defaultValue, String comment) {
        if (node.isVirtual()) {
            node.setValue(defaultValue).setComment(comment);
        }
        return node;
    }

    private CommentedConfigurationNode check(CommentedConfigurationNode node, Object defaultValue) {
        if (node.isVirtual()) {
            node.setValue(defaultValue);
        }
        return node;
    }
}
