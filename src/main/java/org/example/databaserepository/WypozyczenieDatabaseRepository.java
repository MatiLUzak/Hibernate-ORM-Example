package org.example.databaserepository;

import jakarta.persistence.EntityManager;
import org.example.model.Wypozyczenie;
import java.util.UUID;

public class WypozyczenieDatabaseRepository extends DatabaseRepository<Wypozyczenie, UUID> {

    public WypozyczenieDatabaseRepository(EntityManager em) {
        super(em, Wypozyczenie.class);
    }
}
