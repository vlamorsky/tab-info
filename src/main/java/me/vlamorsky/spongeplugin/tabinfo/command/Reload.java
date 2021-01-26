package me.vlamorsky.spongeplugin.tabinfo.command;

import me.vlamorsky.spongeplugin.tabinfo.TabInfo;
import me.vlamorsky.spongeplugin.tabinfo.config.Config;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.IOException;

public class Reload implements CommandExecutor {

    private final Config config;
    private final Logger logger;

    public Reload() {
        config = TabInfo.getInstance().getConfig();
        logger = TabInfo.getInstance().getLogger();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        try {
            config.loadConfiguration();

            logger.info("Configurations reloaded successfully.");
            src.sendMessage(Text.of(TextColors.GREEN, TabInfo.NAME + ": configurations reloaded successfully."));

        } catch (IOException | ObjectMappingException e) {

            logger.warn("Error while reloading configurations.");
            src.sendMessage(Text.of(TextColors.RED, TabInfo.NAME + ": error while reloading configurations."));

            e.printStackTrace();
        }

        return CommandResult.success();
    }
}
