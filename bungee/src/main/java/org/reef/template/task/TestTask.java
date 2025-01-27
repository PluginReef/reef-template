package org.reef.template.task;

import eu.okaeri.commons.cache.Cached;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.platform.bungee.annotation.Scheduled;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.concurrent.TimeUnit;

@Scheduled(rate = 5, timeUnit = TimeUnit.MINUTES)
public class TestTask implements Runnable {

    private @Inject ProxyServer proxy;
    private @Inject("cachedDbData") Cached<String> cachedData;

    @Override
    public void run() {
        String cachedValue = this.cachedData.get();
        this.proxy.broadcast(TextComponent.fromLegacyText(cachedValue));

        String updatedValue = this.cachedData.update();
        this.proxy.broadcast(TextComponent.fromLegacyText(updatedValue));
    }
}