package org.reef.template.controller;

import eu.okaeri.commands.bungee.handler.CommandsUnknownErrorEvent;
import eu.okaeri.commands.service.CommandData;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.bungee.scheduler.PlatformScheduler;
import eu.okaeri.platform.core.annotation.Component;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.reef.template.persistence.PlayerProperties;
import org.reef.template.persistence.PlayerRepository;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;

@Component
public class TestController implements Listener {

    private @Inject PlatformScheduler scheduler;
    private @Inject PlayerRepository playerPersistence;

    @EventHandler
    public void onJoin(ServerConnectEvent event) {

        this.scheduler.runAsync(() -> {

            PlayerProperties playerProperties = this.playerPersistence.get(event.getPlayer());
            Instant lastJoined = playerProperties.getLastJoined();
            event.getPlayer().sendMessage(TextComponent.fromLegacyText("Your last join time: " + lastJoined));
            playerProperties.setLastJoined(Instant.now());

            playerProperties.save();
        });
    }

    @EventHandler
    public void onCommandsUnknownError(CommandsUnknownErrorEvent event) {

        CommandData data = event.getData();
        CommandSender sender = data.get("sender", CommandSender.class);
        if (sender == null) {
            return;
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        event.getCause().printStackTrace(pw);
        sender.sendMessage(sw.toString());
    }
}