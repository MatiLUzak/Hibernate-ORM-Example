package org.example.databasemanagers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.databaserepository.WypozyczenieDatabaseRepository;
import org.example.exceptions.RepozytoriumException;
import org.example.model.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ZarzadcaWypozyczeniaTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private WypozyczenieDatabaseRepository wypozyczenieRepo;
    private ZarzadcaWypozyczenia zarzadca;

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
        wypozyczenieRepo = new WypozyczenieDatabaseRepository(em);
        zarzadca = new ZarzadcaWypozyczenia(wypozyczenieRepo, em);

        // Clean up data before each test
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Wypozyczenie").executeUpdate();
        em.createQuery("DELETE FROM Wypozyczajacy").executeUpdate();
        em.createQuery("DELETE FROM Wolumin").executeUpdate();
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
    void testDodajWypozyczenie() {
        TypWypozyczajacy typ = new TypWypozyczajacy(0.5, 14, 5);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Jan Kowalski", LocalDate.of(1990, 1, 1), "ul. Kwiatowa 10");
        Wolumin wolumin = new Wolumin("Wydawnictwo ABC", "Polski", "Tytuł XYZ");

        em.getTransaction().begin();
        em.persist(typ);
        em.persist(wypozyczajacy);
        em.persist(wolumin);
        em.getTransaction().commit();

        Wypozyczenie wypozyczenie = new Wypozyczenie(wypozyczajacy, wolumin);

        zarzadca.dodajWypozyczenie(wypozyczenie);

        Wypozyczenie retrieved = wypozyczenieRepo.znajdzPoId(wypozyczenie.getId());
        assertNotNull(retrieved);
        assertEquals(wypozyczenie.getId(), retrieved.getId());
    }

    @Test
    void testObliczKare() {
        TypWypozyczajacy typ = new TypWypozyczajacy(0.5, 14, 5);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Anna Nowak", LocalDate.of(1995, 5, 15), "ul. Wiosenna 20");
        Wolumin wolumin = new Wolumin("Wydawnictwo XYZ", "Angielski", "Tytuł ABC");

        em.getTransaction().begin();
        em.persist(typ);
        em.persist(wypozyczajacy);
        em.persist(wolumin);
        em.getTransaction().commit();

        Wypozyczenie wypozyczenie = new Wypozyczenie(wypozyczajacy, wolumin);

        zarzadca.dodajWypozyczenie(wypozyczenie);

        em.getTransaction().begin();
        wypozyczenie.koniecWypozyczenia();
        wypozyczenie.setDataDo(wypozyczenie.getDataOd().plusDays(4));
        em.merge(wypozyczenie);
        em.getTransaction().commit();

        double kara = zarzadca.obliczKare(wypozyczenie.getId());

        assertEquals(2.0, kara, 0.001); // 0.5 * 4 days = 2.0
    }

    @Test
    void testUsunWypozyczenie() {
        TypWypozyczajacy typ = new TypWypozyczajacy(1.0, 30, 10);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Marek Zieliński", LocalDate.of(1985, 3, 10), "ul. Letnia 5");
        Wolumin wolumin = new Wolumin("Wydawnictwo DEF", "Niemiecki", "Tytuł LMN");

        em.getTransaction().begin();
        em.persist(typ);
        em.persist(wypozyczajacy);
        em.persist(wolumin);
        em.getTransaction().commit();

        Wypozyczenie wypozyczenie = new Wypozyczenie(wypozyczajacy, wolumin);

        zarzadca.dodajWypozyczenie(wypozyczenie);

        UUID wypozyczenieId = wypozyczenie.getId();

        zarzadca.usunWypozyczenie(wypozyczenieId);

        Wypozyczenie retrieved = wypozyczenieRepo.znajdzPoId(wypozyczenieId);
        assertNull(retrieved);

    }


    @Test
    void testUsunWypozyczenieNotFound() {
        UUID fakeId = UUID.randomUUID();

        RepozytoriumException exception = assertThrows(RepozytoriumException.class, () -> {
            zarzadca.usunWypozyczenie(fakeId);
        });

        assertEquals("Nie znaleziono wypożyczenia", exception.getMessage());
    }



    @Test
    void testPessimisticLocking() throws InterruptedException {
        TypWypozyczajacy typ = new TypWypozyczajacy(0.5, 14, 5);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Ewa Kwiatkowska", LocalDate.of(1992, 7, 25), "ul. Jesienna 15");
        Wolumin wolumin = new Wolumin("Wydawnictwo GHI", "Hiszpański", "Tytuł OPQ");

        em.getTransaction().begin();
        em.persist(typ);
        em.persist(wypozyczajacy);
        em.persist(wolumin);
        em.getTransaction().commit();

        Wypozyczenie wypozyczenie = new Wypozyczenie(wypozyczajacy, wolumin);

        zarzadca.dodajWypozyczenie(wypozyczenie);

        UUID wypozyczenieId = wypozyczenie.getId();

        em.getTransaction().begin();
        Wypozyczenie lockedWypozyczenie = wypozyczenieRepo.znajdzIZamknijPoId(wypozyczenieId);
        Thread thread = new Thread(() -> {
            EntityManager em2 = emf.createEntityManager();
            WypozyczenieDatabaseRepository repo2 = new WypozyczenieDatabaseRepository(em2);
            ZarzadcaWypozyczenia zarzadca2 = new ZarzadcaWypozyczenia(repo2, em2);

            try {
                zarzadca2.usunWypozyczenie(wypozyczenieId);
            } catch (RepozytoriumException e) {
                assertTrue(e.getMessage().contains("Nie można uzyskać blokady na wypożyczenie"));
            } finally {
                em2.close();
            }
        });

        thread.start();

        Thread.sleep(1000);

        em.getTransaction().commit();

        thread.join();

        Wypozyczenie retrieved = wypozyczenieRepo.znajdzPoId(wypozyczenieId);
        assertNotNull(retrieved);
    }


}
