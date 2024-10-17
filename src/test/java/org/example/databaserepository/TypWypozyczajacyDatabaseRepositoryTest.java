package org.example.databaserepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.example.model.TypWypozyczajacy;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TypWypozyczajacyDatabaseRepositoryTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private TypWypozyczajacyDatabaseRepository repository;

    @BeforeAll
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("POSTGRES_RENT_PU");
    }

    @BeforeEach
    public void setUp() {
        em = emf.createEntityManager();
        repository = new TypWypozyczajacyDatabaseRepository(em);
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
    public void testDodajTypWypozyczajacy() {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        TypWypozyczajacy typWypozyczajacy = new TypWypozyczajacy(10.0, 30, 5);
        repository.dodaj(typWypozyczajacy);

        //assertNotNull(typWypozyczajacy.getId());

        transaction.commit();
    }
}
