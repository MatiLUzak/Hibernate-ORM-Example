package org.example.model;
import jakarta.persistence.*;

@MappedSuperclass
public abstract class AbstractEntity {

    @Version
    private long version;
}
