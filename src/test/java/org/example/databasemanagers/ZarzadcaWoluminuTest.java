package org.example.databasemanagers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.databaserepository.WoluminDatabaseRepository;
import org.example.exceptions.RepozytoriumException;
import org.example.model.Wolumin;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ZarzadcaWoluminuTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private WoluminDatabaseRepository woluminRepo;
    private ZarzadcaWoluminu zarzadca;

    @BeforeAll
    static void init() {
        emf = Persistence.createEntityManagerFactory("POSTGRES_RENT_PU");
    }

    @AfterAll
    static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        woluminRepo = new WoluminDatabaseRepository(em);
        zarzadca = new ZarzadcaWoluminu(woluminRepo, em);

        // Clean up data before each test
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Wolumin").executeUpdate();
        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    @Test
    void testDodajWolumin() {
        // Arrange
        Wolumin wolumin = new Wolumin("Wydawnictwo ABC", "Polski", "Tytuł XYZ");

        // Act
        zarzadca.dodajWolumin(wolumin);

        // Assert
        Wolumin retrieved = woluminRepo.znajdzPoId(wolumin.getId());
        assertNotNull(retrieved);
        assertEquals(wolumin.getId(), retrieved.getId());
    }

    @Test
    void testUsunWolumin() {
        // Arrange
        Wolumin wolumin = new Wolumin("Wydawnictwo DEF", "Angielski", "Tytuł LMN");

        // Dodaj wolumin
        zarzadca.dodajWolumin(wolumin);

        UUID woluminId = wolumin.getId();

        // Act
        zarzadca.usunWolumin(woluminId);

        // Assert
        Wolumin retrieved = woluminRepo.znajdzPoId(woluminId);
        assertNull(retrieved);
    }

    @Test
    void testUsunWoluminNotFound() {
        UUID fakeId = UUID.randomUUID();

        RepozytoriumException exception = assertThrows(RepozytoriumException.class, () -> {
            zarzadca.usunWolumin(fakeId);
        });

        assertEquals("Nie znaleziono woluminu", exception.getMessage());
    }

    @Test
    void testPessimisticLocking() throws InterruptedException {
        // Arrange
        Wolumin wolumin = new Wolumin("Wydawnictwo GHI", "Niemiecki", "Tytuł OPQ");

        // Dodaj wolumin
        zarzadca.dodajWolumin(wolumin);

        UUID woluminId = wolumin.getId();

        // Begin transaction and lock the entity
        em.getTransaction().begin();
        Wolumin lockedWolumin = woluminRepo.znajdzIZamknijPoId(woluminId);

        // Set up a separate thread to simulate concurrent access
        Thread thread = new Thread(() -> {
            EntityManager em2 = emf.createEntityManager();
            WoluminDatabaseRepository repo2 = new WoluminDatabaseRepository(em2);
            ZarzadcaWoluminu zarzadca2 = new ZarzadcaWoluminu(repo2, em2);

            try {
                zarzadca2.usunWolumin(woluminId);
            } catch (RepozytoriumException e) {
                // Oczekujemy wyjątku z powodu blokady pesymistycznej
                assertTrue(e.getMessage().contains("Nie można uzyskać blokady na wolumin"));
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
        Wolumin retrieved = woluminRepo.znajdzPoId(woluminId);
        assertNotNull(retrieved);
    }
}
