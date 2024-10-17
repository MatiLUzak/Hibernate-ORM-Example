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
        em.createQuery("DELETE FROM Wypozyczenie").executeUpdate();
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
        TypWypozyczajacy typ = new TypWypozyczajacy(0.5, 14, 5);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Jan Kowalski", LocalDate.of(1990, 1, 1), "ul. Kwiatowa 10");

        em.getTransaction().begin();
        em.persist(typ);
        em.getTransaction().commit();

        zarzadca.dodajWypozyczajacy(wypozyczajacy);

        Wypozyczajacy retrieved = wypozyczajacyRepo.znajdzPoId(wypozyczajacy.getId());
        assertNotNull(retrieved);
        assertEquals(wypozyczajacy.getId(), retrieved.getId());
    }

    @Test
    void testUsunWypozyczajacy() {
        TypWypozyczajacy typ = new TypWypozyczajacy(1.0, 30, 10);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Anna Nowak", LocalDate.of(1985, 5, 15), "ul. Wiosenna 20");

        em.getTransaction().begin();
        em.persist(typ);
        em.getTransaction().commit();

        zarzadca.dodajWypozyczajacy(wypozyczajacy);

        UUID wypozyczajacyId = wypozyczajacy.getId();

        zarzadca.usunWypozyczajacy(wypozyczajacyId);

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
        TypWypozyczajacy typ = new TypWypozyczajacy(0.5, 14, 5);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Ewa Kwiatkowska", LocalDate.of(1992, 7, 25), "ul. Jesienna 15");

        em.getTransaction().begin();
        em.persist(typ);
        em.getTransaction().commit();

        zarzadca.dodajWypozyczajacy(wypozyczajacy);

        UUID wypozyczajacyId = wypozyczajacy.getId();

        em.getTransaction().begin();
        Wypozyczajacy lockedWypozyczajacy = wypozyczajacyRepo.znajdzIZamknijPoId(wypozyczajacyId);

        Thread thread = new Thread(() -> {
            EntityManager em2 = emf.createEntityManager();
            WypozyczajacyDatabaseRepository repo2 = new WypozyczajacyDatabaseRepository(em2);
            ZarzadcaWypozyczajacy zarzadca2 = new ZarzadcaWypozyczajacy(repo2, em2);

            try {
                zarzadca2.usunWypozyczajacy(wypozyczajacyId);
            } catch (RepozytoriumException e) {
                assertTrue(e.getMessage().contains("Nie można uzyskać blokady na wypożyczającego"));
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

        Wypozyczajacy retrieved = wypozyczajacyRepo.znajdzPoId(wypozyczajacyId);
        assertNotNull(retrieved);
    }

}
