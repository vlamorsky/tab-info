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
    public boolean FOOTER_TPS_ENABLED;
    public boolean FOOTER_PING_ENABLED;

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

        FOOTER_TPS_ENABLED = check(
                node.getNode("footer", "tps-enabled"),
                true)
                .getBoolean();

        FOOTER_PING_ENABLED = check(
                node.getNode("footer", "ping-enabled"),
                true)
                .getBoolean();

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
