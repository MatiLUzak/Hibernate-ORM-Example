package org.example.model;
import jakarta.persistence.*;

import java.util.UUID;

@MappedSuperclass
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Version
    private long version;

    public UUID getId() {
        return id;
    }

    public long getVersion() {
        return version;
    }
}
