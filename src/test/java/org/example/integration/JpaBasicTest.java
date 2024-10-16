package org.example.integration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.example.model.TypWypozyczajacy;
import org.example.model.Wolumin;
import org.example.model.Wypozyczajacy;
import org.example.model.Wypozyczenie;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JpaBasicTest {

    private static EntityManagerFactory emf;
    private EntityManager em;

    @BeforeAll
    public static void setUpClass() {
        // Tworzymy EntityManagerFactory na początku
        emf = Persistence.createEntityManagerFactory("POSTGRES_RENT_PU");
    }

    @BeforeEach
    public void setUp() {
        // Tworzymy EntityManager przed każdym testem
        em = emf.createEntityManager();
    }

    @AfterEach
    public void tearDown() {
        // Zamykanie EntityManager po każdym teście
        if (em != null) {
            em.close();
        }
    }

    @AfterAll
    public static void tearDownClass() {
        // Zamykanie EntityManagerFactory po zakończeniu wszystkich testów
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    public void testPersistWypozyczajacy() {
        // Rozpoczynamy transakcję
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        // Tworzymy obiekt TypWypozyczajacy
        TypWypozyczajacy typWypozyczajacy = new TypWypozyczajacy(10.0, 30, 5);
        em.persist(typWypozyczajacy); // Zapisujemy TypWypozyczajacy do bazy

        // Tworzymy obiekt Wypozyczajacy
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typWypozyczajacy, "Jan Kowalski", LocalDate.of(1990, 1, 1), "Ulica 123, Miasto");
        em.persist(wypozyczajacy); // Zapisujemy Wypozyczajacy do bazy

        // Sprawdzamy, czy obiekt ma wygenerowane UUID
        assertNotNull(wypozyczajacy.getUuid());

        // Zatwierdzamy transakcję
        transaction.commit();
    }

    @Test
    public void testPersistWypozyczenie() {
        // Rozpoczynamy transakcję
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        // Tworzymy obiekt TypWypozyczajacy
        TypWypozyczajacy typWypozyczajacy = new TypWypozyczajacy(10.0, 30, 5);
        em.persist(typWypozyczajacy); // Zapisujemy TypWypozyczajacy do bazy

        // Tworzymy obiekt Wypozyczajacy
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typWypozyczajacy, "Jan Kowalski", LocalDate.of(1990, 1, 1), "Ulica 123, Miasto");
        em.persist(wypozyczajacy); // Zapisujemy Wypozyczajacy do bazy

        // Tworzymy obiekt Wolumin
        Wolumin wolumin = new Wolumin("Wydawnictwo", "Polski", "Tytuł Książki");
        em.persist(wolumin); // Zapisujemy Wolumin do bazy

        // Tworzymy obiekt Wypozyczenie
        Wypozyczenie wypozyczenie = new Wypozyczenie(wypozyczajacy, wolumin);
        em.persist(wypozyczenie); // Zapisujemy Wypozyczenie do bazy

        // Sprawdzamy, czy obiekt ma wygenerowane UUID
        assertNotNull(wypozyczenie.getUuid());

        // Zatwierdzamy transakcję
        transaction.commit();
    }
}

