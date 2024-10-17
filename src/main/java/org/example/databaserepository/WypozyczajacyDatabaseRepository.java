package org.example.databaserepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.example.model.Wypozyczajacy;
import java.util.UUID;

public class WypozyczajacyDatabaseRepository extends DatabaseRepository<Wypozyczajacy, UUID> {

    public WypozyczajacyDatabaseRepository(EntityManager em) {
        super(em, Wypozyczajacy.class);
    }

    public Wypozyczajacy znajdzIZamknijPoId(UUID id) {
        return em.find(Wypozyczajacy.class, id, LockModeType.PESSIMISTIC_WRITE);
    }
}
