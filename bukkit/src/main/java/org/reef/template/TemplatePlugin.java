package org.reef.template;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.zaxxer.hikari.HikariConfig;
import eu.okaeri.configs.json.simple.JsonSimpleConfigurer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.persistence.PersistencePath;
import eu.okaeri.persistence.document.DocumentPersistence;
import eu.okaeri.persistence.jdbc.H2Persistence;
import eu.okaeri.persistence.jdbc.MariaDbPersistence;
import eu.okaeri.persistence.mongo.MongoPersistence;
import eu.okaeri.persistence.redis.RedisPersistence;
import eu.okaeri.platform.bukkit.OkaeriBukkitPlugin;
import eu.okaeri.platform.bukkit.persistence.YamlBukkitPersistence;
import eu.okaeri.platform.core.annotation.Bean;
import eu.okaeri.platform.core.annotation.Scan;
import eu.okaeri.platform.core.plan.ExecutionPhase;
import eu.okaeri.platform.core.plan.Planned;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.reef.template.config.PluginConfig;

import java.io.File;

@Scan(exclusions = "org.reef.template.libs", deep = true)
public class TemplatePlugin extends OkaeriBukkitPlugin {

    @Planned(ExecutionPhase.STARTUP)
    public void onStartup() {
        this.getLogger().info("Enabled!");
    }

    @Planned(ExecutionPhase.SHUTDOWN)
    public void onShutdown() {
        this.getLogger().info("Disabled!");
    }

    @Bean(value = "persistence")
    public DocumentPersistence configurePersistence(@Inject("dataFolder") File dataFolder, PluginConfig config) {

        try { Class.forName("org.mariadb.jdbc.Driver"); } catch (ClassNotFoundException ignored) { }
        try { Class.forName("org.h2.Driver"); } catch (ClassNotFoundException ignored) { }

        PersistencePath basePath = PersistencePath.of(config.getStorage().getPrefix());

        switch (config.getStorage().getBackend()) {
            case FLAT:
                return YamlBukkitPersistence.of(new File(dataFolder, "storage"));
            case REDIS:
                RedisURI redisUri = RedisURI.create(config.getStorage().getUri());
                RedisClient redisClient = RedisClient.create(redisUri);
                return new DocumentPersistence(new RedisPersistence(basePath, redisClient), JsonSimpleConfigurer::new, new SerdesBukkit());
            case MONGO:
                ConnectionString mongoUri = new ConnectionString(config.getStorage().getUri());
                MongoClient mongoClient = MongoClients.create(mongoUri);
                if (mongoUri.getDatabase() == null) {
                    throw new IllegalArgumentException("Mongo URI needs to specify the database");
                }
                return new DocumentPersistence(new MongoPersistence(basePath, mongoClient, mongoUri.getDatabase()), JsonSimpleConfigurer::new, new SerdesBukkit());
            case MYSQL:
                HikariConfig mariadbHikari = new HikariConfig();
                mariadbHikari.setJdbcUrl(config.getStorage().getUri());
                return new DocumentPersistence(new MariaDbPersistence(basePath, mariadbHikari), JsonSimpleConfigurer::new, new SerdesBukkit());
            case H2:
                HikariConfig jdbcHikari = new HikariConfig();
                jdbcHikari.setJdbcUrl(config.getStorage().getUri());
                return new DocumentPersistence(new H2Persistence(basePath, jdbcHikari), JsonSimpleConfigurer::new, new SerdesBukkit());
            default:
                throw new RuntimeException("unsupported storage backend: " + config.getStorage().getBackend());
        }
    }
}
