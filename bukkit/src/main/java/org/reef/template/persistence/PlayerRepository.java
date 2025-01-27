package org.reef.template.persistence;

import eu.okaeri.persistence.document.DocumentPersistence;
import eu.okaeri.persistence.repository.DocumentRepository;
import eu.okaeri.persistence.repository.annotation.DocumentCollection;
import eu.okaeri.persistence.repository.annotation.DocumentIndex;
import eu.okaeri.persistence.repository.annotation.DocumentPath;
import eu.okaeri.platform.core.annotation.DependsOn;
import org.bukkit.OfflinePlayer;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@DependsOn(
    type = DocumentPersistence.class,
    name = "persistence"
)
@DocumentCollection(path = "player", keyLength = 36, indexes = {
    @DocumentIndex(path = "name", maxLength = 24),
    @DocumentIndex(path = "lastJoinedLocation.world", maxLength = 64)
})
public interface PlayerRepository extends DocumentRepository<UUID, PlayerProperties> {

    @DocumentPath("name")
    Optional<PlayerProperties> findByName(String name);

    @DocumentPath("lastJoinedLocation.world")
    Stream<PlayerProperties> findByLastJoinedLocationWorld(String name);

    @DocumentPath("lastJoinedLocation.y")
    Stream<PlayerProperties> findByLastJoinedLocationY(int y);

    default PlayerProperties get(OfflinePlayer player) {

        PlayerProperties properties = this.findOrCreateByPath(player.getUniqueId());

        if (player.getName() != null) {
            properties.setName(player.getName());
        }

        return properties;
    }
}
