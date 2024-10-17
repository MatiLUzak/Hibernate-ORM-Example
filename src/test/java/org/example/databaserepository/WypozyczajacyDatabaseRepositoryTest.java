package org.example.databaserepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.example.model.TypWypozyczajacy;
import org.example.model.Wypozyczajacy;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class WypozyczajacyDatabaseRepositoryTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private WypozyczajacyDatabaseRepository repository;

    @BeforeAll
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("POSTGRES_RENT_PU");
    }

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        repository = new WypozyczajacyDatabaseRepository(em);
    }

    @AfterEach
    public void tearDown() {
        if (em != null) {
            em.close();
        }
    }

    @AfterAll
    public static void tearDownClass() {
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    public void testDodajWypozyczajacy() {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        TypWypozyczajacy typWypozyczajacy = new TypWypozyczajacy(10.0, 30, 5);
        em.persist(typWypozyczajacy);

        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typWypozyczajacy, "Jan Kowalski", LocalDate.of(1990, 1, 1), "Ulica 123, Miasto");
        repository.dodaj(wypozyczajacy);

        assertNotNull(wypozyczajacy.getId());

        transaction.commit();
    }
}
