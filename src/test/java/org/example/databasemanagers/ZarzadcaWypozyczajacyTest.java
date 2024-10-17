package org.example.databasemanagers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.databaserepository.WypozyczajacyDatabaseRepository;
import org.example.exceptions.RepozytoriumException;
import org.example.model.TypWypozyczajacy;
import org.example.model.Wypozyczajacy;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ZarzadcaWypozyczajacyTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private WypozyczajacyDatabaseRepository wypozyczajacyRepo;
    private ZarzadcaWypozyczajacy zarzadca;

    @BeforeAll
    static void init() {
        emf = Persistence.createEntityManagerFactory("POSTGRES_RENT_PU");
    }

    @AfterAll
    static void close() {
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        wypozyczajacyRepo = new WypozyczajacyDatabaseRepository(em);
        zarzadca = new ZarzadcaWypozyczajacy(wypozyczajacyRepo, em);

        // Clean up data before each test
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Wypozyczajacy").executeUpdate();
        em.createQuery("DELETE FROM TypWypozyczajacy").executeUpdate();
        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (em != null) {
            em.close();
        }
    }

    @Test
    void testDodajWypozyczajacy() {
        // Arrange
        TypWypozyczajacy typ = new TypWypozyczajacy(0.5, 14, 5);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Jan Kowalski", LocalDate.of(1990, 1, 1), "ul. Kwiatowa 10");

        // Persist TypWypozyczajacy
        em.getTransaction().begin();
        em.persist(typ);
        em.getTransaction().commit();

        // Act
        zarzadca.dodajWypozyczajacy(wypozyczajacy);

        // Assert
        Wypozyczajacy retrieved = wypozyczajacyRepo.znajdzPoId(wypozyczajacy.getId());
        assertNotNull(retrieved);
        assertEquals(wypozyczajacy.getId(), retrieved.getId());
    }

    @Test
    void testUsunWypozyczajacy() {
        // Arrange
        TypWypozyczajacy typ = new TypWypozyczajacy(1.0, 30, 10);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Anna Nowak", LocalDate.of(1985, 5, 15), "ul. Wiosenna 20");

        // Persist TypWypozyczajacy
        em.getTransaction().begin();
        em.persist(typ);
        em.getTransaction().commit();

        // Dodaj wypożyczającego (metoda zarządza transakcją)
        zarzadca.dodajWypozyczajacy(wypozyczajacy);

        UUID wypozyczajacyId = wypozyczajacy.getId();

        // Act
        zarzadca.usunWypozyczajacy(wypozyczajacyId);

        // Assert
        Wypozyczajacy retrieved = wypozyczajacyRepo.znajdzPoId(wypozyczajacyId);
        assertNull(retrieved);
    }



    @Test
    void testUsunWypozyczajacyNotFound() {
        UUID fakeId = UUID.randomUUID();

        RepozytoriumException exception = assertThrows(RepozytoriumException.class, () -> {
            zarzadca.usunWypozyczajacy(fakeId);
        });

        assertEquals("Nie znaleziono wypożyczającego", exception.getMessage());
    }

    @Test
    void testPessimisticLocking() throws InterruptedException {
        // Arrange
        TypWypozyczajacy typ = new TypWypozyczajacy(0.5, 14, 5);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Ewa Kwiatkowska", LocalDate.of(1992, 7, 25), "ul. Jesienna 15");

        // Persist TypWypozyczajacy
        em.getTransaction().begin();
        em.persist(typ);
        em.getTransaction().commit();

        // Dodaj wypożyczającego (metoda zarządza transakcją)
        zarzadca.dodajWypozyczajacy(wypozyczajacy);

        UUID wypozyczajacyId = wypozyczajacy.getId();

        // Begin transaction and lock the entity
        em.getTransaction().begin();
        Wypozyczajacy lockedWypozyczajacy = wypozyczajacyRepo.znajdzIZamknijPoId(wypozyczajacyId);

        // Set up a separate thread to simulate concurrent access
        Thread thread = new Thread(() -> {
            EntityManager em2 = emf.createEntityManager();
            WypozyczajacyDatabaseRepository repo2 = new WypozyczajacyDatabaseRepository(em2);
            ZarzadcaWypozyczajacy zarzadca2 = new ZarzadcaWypozyczajacy(repo2, em2);

            try {
                zarzadca2.usunWypozyczajacy(wypozyczajacyId);
            } catch (RepozytoriumException e) {
                // Oczekujemy wyjątku z powodu blokady pesymistycznej
                assertTrue(e.getMessage().contains("Nie można uzyskać blokady na wypożyczającego"));
            } finally {
                if (em2.isOpen()) {
                    em2.close();
                }
            }
        });

        // Start the other thread
        thread.start();

        // Wait to ensure the other thread attempts to acquire the lock
        Thread.sleep(1000);

        // Release the lock
        em.getTransaction().commit();

        // Wait for the other thread to finish
        thread.join();

        // Verify that the entity still exists (since the other thread couldn't delete it)
        Wypozyczajacy retrieved = wypozyczajacyRepo.znajdzPoId(wypozyczajacyId);
        assertNotNull(retrieved);
    }

}
