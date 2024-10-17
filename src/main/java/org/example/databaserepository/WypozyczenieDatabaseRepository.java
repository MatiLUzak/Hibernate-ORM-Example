package org.example.databaserepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.example.model.Wypozyczenie;
import java.util.UUID;

public class WypozyczenieDatabaseRepository extends DatabaseRepository<Wypozyczenie, UUID> {

    public WypozyczenieDatabaseRepository(EntityManager em) {
        super(em, Wypozyczenie.class);
    }
    public Wypozyczenie znajdzIZamknijPoId(UUID id) {
        return em.find(Wypozyczenie.class, id, LockModeType.PESSIMISTIC_WRITE);
    }

}
