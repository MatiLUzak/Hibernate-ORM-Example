package org.example.databaserepository;

import jakarta.persistence.EntityManager;
import org.example.model.TypWypozyczajacy;

public class TypWypozyczajacyDatabaseRepository extends DatabaseRepository<TypWypozyczajacy, Long> {

    public TypWypozyczajacyDatabaseRepository(EntityManager em) {
        super(em, TypWypozyczajacy.class);
    }
}
