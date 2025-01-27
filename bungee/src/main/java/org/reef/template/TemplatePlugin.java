package org.reef.template;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.zaxxer.hikari.HikariConfig;
import eu.okaeri.configs.json.simple.JsonSimpleConfigurer;
import eu.okaeri.injector.annotation.Inject;
import eu.okaeri.persistence.PersistencePath;
import eu.okaeri.persistence.document.DocumentPersistence;
import eu.okaeri.persistence.jdbc.H2Persistence;
import eu.okaeri.persistence.jdbc.MariaDbPersistence;
import eu.okaeri.persistence.mongo.MongoPersistence;
import eu.okaeri.persistence.redis.RedisPersistence;
import eu.okaeri.platform.bungee.OkaeriBungeePlugin;
import eu.okaeri.platform.bungee.persistence.YamlBungeePersistence;
import eu.okaeri.platform.core.annotation.Bean;
import eu.okaeri.platform.core.annotation.Scan;
import eu.okaeri.platform.core.plan.ExecutionPhase;
import eu.okaeri.platform.core.plan.Planned;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.reef.template.config.PluginConfig;

import java.io.File;

@Scan(exclusions = "org.reef.template.libs", deep = true)
public class TemplatePlugin extends OkaeriBungeePlugin {

    @Planned(ExecutionPhase.STARTUP)
    public void onStartup() {
        this.getLogger().info("Enabled!");
    }

    @Planned(ExecutionPhase.SHUTDOWN)
    public void onShutdown() {
        this.getLogger().info("Disabled!");
    }

    @Bean("persistence")
    public DocumentPersistence configurePersistence(@Inject("dataFolder") File dataFolder, PluginConfig pluginConfig) {

        try { Class.forName("org.mariadb.jdbc.Driver"); } catch (ClassNotFoundException ignored) { }
        try { Class.forName("org.h2.Driver"); } catch (ClassNotFoundException ignored) { }

        PersistencePath basePath = PersistencePath.of(pluginConfig.getStorage().getPrefix());

        switch (pluginConfig.getStorage().getBackend()) {
            case FLAT:
                return YamlBungeePersistence.of(new File(dataFolder, "storage"));
            case REDIS:
                RedisURI redisUri = RedisURI.create(pluginConfig.getStorage().getUri());
                RedisClient redisClient = RedisClient.create(redisUri);
                return new DocumentPersistence(new RedisPersistence(basePath, redisClient), JsonSimpleConfigurer::new);
            case MONGO:
                ConnectionString mongoUri = new ConnectionString(pluginConfig.getStorage().getUri());
                MongoClient mongoClient = MongoClients.create(mongoUri);
                if (mongoUri.getDatabase() == null) {
                    throw new IllegalArgumentException("Mongo URI needs to specify the database");
                }
                return new DocumentPersistence(new MongoPersistence(basePath, mongoClient, mongoUri.getDatabase()), JsonSimpleConfigurer::new);
            case MYSQL:
                HikariConfig mariadbHikari = new HikariConfig();
                mariadbHikari.setJdbcUrl(pluginConfig.getStorage().getUri());
                return new DocumentPersistence(new MariaDbPersistence(basePath, mariadbHikari), JsonSimpleConfigurer::new);
            case H2:
                HikariConfig jdbcHikari = new HikariConfig();
                jdbcHikari.setJdbcUrl(pluginConfig.getStorage().getUri());
                return new DocumentPersistence(new H2Persistence(basePath, jdbcHikari), JsonSimpleConfigurer::new);
            default:
                throw new RuntimeException("unsupported storage backend: " + pluginConfig.getStorage().getBackend());
        }
    }
}
