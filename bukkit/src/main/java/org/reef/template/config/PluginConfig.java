package org.reef.template.config;

import eu.okaeri.commons.bukkit.item.ItemStackBuilder;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.*;
import eu.okaeri.platform.core.annotation.Configuration;
import eu.okaeri.validator.annotation.Size;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.reef.template.persistence.StorageBackend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Getter
@Setter
@Configuration
@Header("==     Reef-Template     ==")
@Header("       Plugin Config      ")
@Header("==     Reef-LobbySystem     ==")
public class PluginConfig extends OkaeriConfig {

    @Comment("Storage settings")
    private StorageConfig storage = new StorageConfig();

    @Getter
    @Setter
    public static class StorageConfig extends OkaeriConfig {

        @Variable("OPE_STORAGE_BACKEND")
        @Comment("Type of the storage backend: FLAT, REDIS, MONGO, MYSQL, H2")
        private StorageBackend backend = StorageBackend.FLAT;

        @Variable("OPE_STORAGE_PREFIX")
        @Comment("Prefix for the storage: allows to have multiple instances using same database")
        @Comment("FLAT   : no effect due to local nature")
        @Comment("REDIS  : {storagePrefix}:{collection} -> reef_player:player")
        @Comment("MONGO  : {storagePrefix}:{collection} -> reef_player")
        @Comment("MYSQL  : {storagePrefix}:{collection} -> reef_player")
        @Comment("H2     : {storagePrefix}:{collection} -> reef_player")
        private String prefix = "ope";

        @Variable("OPE_STORAGE_URI")
        @Comment("FLAT   : not applicable, plugin controlled")
        @Comment("REDIS  : redis://localhost")
        @Comment("MONGO  : mongodb://localhost:27017/db")
        @Comment("MYSQL  : jdbc:mysql://localhost:3306/db?user=root&password=1234")
        @Comment("H2     : jdbc:h2:file:./plugins/Reef-Template/storage;mode=mysql")
        private String uri = "redis://localhost";
    }
}
