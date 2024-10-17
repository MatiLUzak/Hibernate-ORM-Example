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

        em.getTransaction().begin();
        em.createQuery("DELETE FROM Wypozyczenie").executeUpdate();
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
        Wolumin wolumin = new Wolumin("Wydawnictwo ABC", "Polski", "Tytuł XYZ");

        zarzadca.dodajWolumin(wolumin);

        Wolumin retrieved = woluminRepo.znajdzPoId(wolumin.getId());
        assertNotNull(retrieved);
        assertEquals(wolumin.getId(), retrieved.getId());
    }

    @Test
    void testUsunWolumin() {
        Wolumin wolumin = new Wolumin("Wydawnictwo DEF", "Angielski", "Tytuł LMN");

        zarzadca.dodajWolumin(wolumin);

        UUID woluminId = wolumin.getId();

        zarzadca.usunWolumin(woluminId);

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
        Wolumin wolumin = new Wolumin("Wydawnictwo GHI", "Niemiecki", "Tytuł OPQ");

        zarzadca.dodajWolumin(wolumin);

        UUID woluminId = wolumin.getId();

        em.getTransaction().begin();
        Wolumin lockedWolumin = woluminRepo.znajdzIZamknijPoId(woluminId);

        Thread thread = new Thread(() -> {
            EntityManager em2 = emf.createEntityManager();
            WoluminDatabaseRepository repo2 = new WoluminDatabaseRepository(em2);
            ZarzadcaWoluminu zarzadca2 = new ZarzadcaWoluminu(repo2, em2);

            try {
                zarzadca2.usunWolumin(woluminId);
            } catch (RepozytoriumException e) {
                assertTrue(e.getMessage().contains("Nie można uzyskać blokady na wolumin"));
            } finally {
                if (em2.isOpen()) {
                    em2.close();
                }
            }
        });

        thread.start();

        Thread.sleep(1000);

        em.getTransaction().commit();

        thread.join();

        Wolumin retrieved = woluminRepo.znajdzPoId(woluminId);
        assertNotNull(retrieved);
    }
}
