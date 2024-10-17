package org.example.databaserepository;

import jakarta.persistence.EntityManager;
import org.example.model.Wypozyczajacy;
import java.util.UUID;

public class WypozyczajacyDatabaseRepository extends DatabaseRepository<Wypozyczajacy, UUID> {

    public WypozyczajacyDatabaseRepository(EntityManager em) {
        super(em, Wypozyczajacy.class);
    }
}
