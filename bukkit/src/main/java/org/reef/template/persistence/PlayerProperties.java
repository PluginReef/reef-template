package org.reef.template.persistence;

import eu.okaeri.persistence.document.Document;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Location;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class PlayerProperties extends Document {

    private String name;
    private Instant lastJoined;
    private Location lastJoinedLocation;

    public UUID getUniqueId() {
        return this.getPath().toUUID();
    }
}
