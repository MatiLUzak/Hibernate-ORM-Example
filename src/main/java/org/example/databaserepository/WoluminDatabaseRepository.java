package org.example.databaserepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.example.model.Wolumin;

import java.util.UUID;

public class WoluminDatabaseRepository extends DatabaseRepository<Wolumin, UUID> {

    public WoluminDatabaseRepository(EntityManager em) {
        super(em, Wolumin.class);
    }

    public Wolumin znajdzIZamknijPoId(UUID id) {
        return em.find(Wolumin.class, id, LockModeType.PESSIMISTIC_WRITE);
    }
}
