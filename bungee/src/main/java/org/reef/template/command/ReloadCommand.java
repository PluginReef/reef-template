package org.reef.template.command;

import eu.okaeri.commands.annotation.*;
import eu.okaeri.commands.bungee.annotation.Async;
import eu.okaeri.commands.bungee.annotation.Sync;
import eu.okaeri.commands.bungee.response.BungeeResponse;
import eu.okaeri.commands.service.CommandService;
import eu.okaeri.i18n.message.Message;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.bungee.i18n.BI18n;
import net.md_5.bungee.api.CommandSender;
import org.reef.template.config.MessageLocaleConfig;
import org.reef.template.config.PluginConfig;

import java.util.logging.Level;
import java.util.logging.Logger;

@Async
@Command(label = "testreload", aliases = "test")
public class ReloadCommand implements CommandService {

    private @Inject PluginConfig pluginConfig;
    private @Inject BI18n i18n;
    private @Inject MessageLocaleConfig messageLocaleConfig;
    private @Inject Logger logger;

    @Executor
    public Message _default(CommandSender sender) {

        try {
            this.pluginConfig.load();
            this.i18n.load();
        } catch (Exception exception) {
            this.logger.log(Level.SEVERE, "Failed to reload configuration", exception);
            return this.i18n.get(sender, this.messageLocaleConfig.getCommandsReloadFail());
        }

        return this.i18n.get(sender, this.messageLocaleConfig.getCommandsReloadSuccess());
    }

}