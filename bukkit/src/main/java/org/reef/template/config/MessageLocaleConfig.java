package org.reef.template.config;

import eu.okaeri.configs.annotation.Header;
import eu.okaeri.i18n.configs.LocaleConfig;
import eu.okaeri.platform.core.annotation.Messages;
import lombok.Getter;

@Getter
@Messages
@Header("==     Reef-Template     ==")
@Header("       Message Config      ")
@Header("==     Reef-LobbySystem     ==")
public class MessageLocaleConfig extends LocaleConfig {

    private String commandsReloadSuccess = "The configuration has been reloaded!";
    private String commandsReloadFail = "Reload fail! See the console for details.";
}
