package org.example.databaserepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.model.TypWypozyczajacy;
import org.example.model.Wolumin;
import org.example.model.Wypozyczenie;
import org.example.model.Wypozyczajacy;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WypozyczenieDatabaseRepositoryTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private WypozyczenieDatabaseRepository repo;

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
        repo = new WypozyczenieDatabaseRepository(em);

        em.getTransaction().begin();
        em.createQuery("DELETE FROM Wypozyczenie").executeUpdate();
        em.createQuery("DELETE FROM Wypozyczajacy").executeUpdate();
        em.createQuery("DELETE FROM Wolumin").executeUpdate();
        em.createQuery("DELETE FROM TypWypozyczajacy").executeUpdate();
        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    @Test
    void testDodajIZnajdzPoId() {
        TypWypozyczajacy typ = new TypWypozyczajacy(1.0, 30, 10);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Jan Nowak", LocalDate.of(1990, 1, 1), "ul. Kwiatowa 10");
        Wolumin wolumin = new Wolumin("Wydawnictwo ABC", "Polski", "Tytuł XYZ");

        em.getTransaction().begin();
        em.persist(typ);
        em.persist(wypozyczajacy);
        em.persist(wolumin);
        em.getTransaction().commit();

        Wypozyczenie wypozyczenie = new Wypozyczenie(wypozyczajacy, wolumin);

        em.getTransaction().begin();
        repo.dodaj(wypozyczenie);
        em.getTransaction().commit();

        Wypozyczenie retrieved = repo.znajdzPoId(wypozyczenie.getId());
        assertNotNull(retrieved);
        assertEquals(wypozyczenie.getId(), retrieved.getId());
    }

    @Test
    void testUpdate() {
        TypWypozyczajacy typ = new TypWypozyczajacy(1.0, 30, 10);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Anna Kowalska", LocalDate.of(1995, 5, 15), "ul. Wiosenna 20");
        Wolumin wolumin = new Wolumin("Wydawnictwo XYZ", "Angielski", "Tytuł ABC");

        em.getTransaction().begin();
        em.persist(typ);
        em.persist(wypozyczajacy);
        em.persist(wolumin);
        em.getTransaction().commit();

        Wypozyczenie wypozyczenie = new Wypozyczenie(wypozyczajacy, wolumin);

        em.getTransaction().begin();
        repo.dodaj(wypozyczenie);
        em.getTransaction().commit();

        em.getTransaction().begin();
        wypozyczenie.koniecWypozyczenia();
        repo.update(wypozyczenie);
        em.getTransaction().commit();

        Wypozyczenie updated = repo.znajdzPoId(wypozyczenie.getId());
        assertNotNull(updated.getDataDo());
    }

    @Test
    void testUsun() {
        TypWypozyczajacy typ = new TypWypozyczajacy(1.0, 30, 10);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Marek Zieliński", LocalDate.of(1985, 3, 10), "ul. Letnia 5");
        Wolumin wolumin = new Wolumin("Wydawnictwo DEF", "Niemiecki", "Tytuł LMN");

        em.getTransaction().begin();
        em.persist(typ);
        em.persist(wypozyczajacy);
        em.persist(wolumin);
        em.getTransaction().commit();

        Wypozyczenie wypozyczenie = new Wypozyczenie(wypozyczajacy, wolumin);

        em.getTransaction().begin();
        repo.dodaj(wypozyczenie);
        em.getTransaction().commit();

        em.getTransaction().begin();
        repo.usun(wypozyczenie);
        em.getTransaction().commit();

        Wypozyczenie retrieved = repo.znajdzPoId(wypozyczenie.getId());
        assertNull(retrieved);
    }

    @Test
    void testZnajdzIZamknijPoId() {
        TypWypozyczajacy typ = new TypWypozyczajacy(1.0, 30, 10);
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(typ, "Ewa Nowak", LocalDate.of(1992, 7, 25), "ul. Jesienna 15");
        Wolumin wolumin = new Wolumin("Wydawnictwo GHI", "Hiszpański", "Tytuł OPQ");

        em.getTransaction().begin();
        em.persist(typ);
        em.persist(wypozyczajacy);
        em.persist(wolumin);
        em.getTransaction().commit();

        Wypozyczenie wypozyczenie = new Wypozyczenie(wypozyczajacy, wolumin);

        em.getTransaction().begin();
        repo.dodaj(wypozyczenie);
        em.getTransaction().commit();

        em.getTransaction().begin();
        Wypozyczenie locked = repo.znajdzIZamknijPoId(wypozyczenie.getId());

        assertNotNull(locked);
        em.getTransaction().commit();
    }
}
