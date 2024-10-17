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
        // Arrange
        TypWypozyczajacy typ = new TypWypozyczajacy(0.5, 14, 5);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Jan Kowalski", LocalDate.of(1990, 1, 1), "ul. Kwiatowa 10");
        Wolumin wolumin = new Wolumin("Wydawnictwo ABC", "Polski", "Tytuł XYZ");

        // Persist dependent entities
        em.getTransaction().begin();
        em.persist(typ);
        em.persist(wypozyczajacy);
        em.persist(wolumin);
        em.getTransaction().commit();

        Wypozyczenie wypozyczenie = new Wypozyczenie(wypozyczajacy, wolumin);

        // Act
        zarzadca.dodajWypozyczenie(wypozyczenie);

        // Assert
        Wypozyczenie retrieved = wypozyczenieRepo.znajdzPoId(wypozyczenie.getId());
        assertNotNull(retrieved);
        assertEquals(wypozyczenie.getId(), retrieved.getId());
    }

    @Test
    void testObliczKare() {
        // Arrange
        TypWypozyczajacy typ = new TypWypozyczajacy(0.5, 14, 5);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Anna Nowak", LocalDate.of(1995, 5, 15), "ul. Wiosenna 20");
        Wolumin wolumin = new Wolumin("Wydawnictwo XYZ", "Angielski", "Tytuł ABC");

        // Persist dependent entities
        em.getTransaction().begin();
        em.persist(typ);
        em.persist(wypozyczajacy);
        em.persist(wolumin);
        em.getTransaction().commit();

        Wypozyczenie wypozyczenie = new Wypozyczenie(wypozyczajacy, wolumin);

        // Add the rental
        zarzadca.dodajWypozyczenie(wypozyczenie);

        // Simulate the end of the rental after 4 days
        em.getTransaction().begin();
        wypozyczenie.koniecWypozyczenia();
        wypozyczenie.setDataDo(wypozyczenie.getDataOd().plusDays(4));
        em.merge(wypozyczenie);
        em.getTransaction().commit();

        // Act
        double kara = zarzadca.obliczKare(wypozyczenie.getId());

        // Assert
        assertEquals(2.0, kara, 0.001); // 0.5 * 4 days = 2.0
    }

    @Test
    void testUsunWypozyczenie() {
        // Arrange
        TypWypozyczajacy typ = new TypWypozyczajacy(1.0, 30, 10);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Marek Zieliński", LocalDate.of(1985, 3, 10), "ul. Letnia 5");
        Wolumin wolumin = new Wolumin("Wydawnictwo DEF", "Niemiecki", "Tytuł LMN");

        // Persist dependent entities
        em.getTransaction().begin();
        em.persist(typ);
        em.persist(wypozyczajacy);
        em.persist(wolumin);
        em.getTransaction().commit();

        Wypozyczenie wypozyczenie = new Wypozyczenie(wypozyczajacy, wolumin);

        // Add the rental
        zarzadca.dodajWypozyczenie(wypozyczenie);

        UUID wypozyczenieId = wypozyczenie.getId();

        // Act
        zarzadca.usunWypozyczenie(wypozyczenieId);

        // Assert
        // Sprawdź, czy wypożyczenie zostało usunięte z bieżących wypożyczeń
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
        // Arrange
        TypWypozyczajacy typ = new TypWypozyczajacy(0.5, 14, 5);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Ewa Kwiatkowska", LocalDate.of(1992, 7, 25), "ul. Jesienna 15");
        Wolumin wolumin = new Wolumin("Wydawnictwo GHI", "Hiszpański", "Tytuł OPQ");

        // Persist dependent entities
        em.getTransaction().begin();
        em.persist(typ);
        em.persist(wypozyczajacy);
        em.persist(wolumin);
        em.getTransaction().commit();

        Wypozyczenie wypozyczenie = new Wypozyczenie(wypozyczajacy, wolumin);

        // Add the rental
        zarzadca.dodajWypozyczenie(wypozyczenie);

        UUID wypozyczenieId = wypozyczenie.getId();

        // Begin transaction and lock the rental
        em.getTransaction().begin();
        Wypozyczenie lockedWypozyczenie = wypozyczenieRepo.znajdzIZamknijPoId(wypozyczenieId);

        // Set up a separate thread to simulate concurrent access
        Thread thread = new Thread(() -> {
            EntityManager em2 = emf.createEntityManager();
            WypozyczenieDatabaseRepository repo2 = new WypozyczenieDatabaseRepository(em2);
            ZarzadcaWypozyczenia zarzadca2 = new ZarzadcaWypozyczenia(repo2, em2);

            try {
                zarzadca2.usunWypozyczenie(wypozyczenieId);
            } catch (RepozytoriumException e) {
                // Oczekujemy wyjątku z powodu blokady pesymistycznej
                assertTrue(e.getMessage().contains("Nie można uzyskać blokady na wypożyczenie"));
            } finally {
                em2.close();
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

        // Sprawdź, czy wypożyczenie nadal istnieje (ponieważ drugi wątek nie mógł go usunąć)
        Wypozyczenie retrieved = wypozyczenieRepo.znajdzPoId(wypozyczenieId);
        assertNotNull(retrieved);
    }


}
