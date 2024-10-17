package org.example.databaserepository;

import jakarta.persistence.*;
import org.example.model.Wolumin;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WoluminDatabaseRepositoryTest {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private WoluminDatabaseRepository repo;

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
        repo = new WoluminDatabaseRepository(em);

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
    void testDodajIZnajdzPoId() {
        Wolumin wolumin = new Wolumin("Wydawnictwo ABC", "Polski", "Tytuł XYZ");

        em.getTransaction().begin();
        repo.dodaj(wolumin);
        em.getTransaction().commit();

        Wolumin retrieved = repo.znajdzPoId(wolumin.getId());
        assertNotNull(retrieved);
        assertEquals(wolumin.getId(), retrieved.getId());
    }

    @Test
    void testUpdate() {
        Wolumin wolumin = new Wolumin("Wydawnictwo DEF", "Angielski", "Tytuł LMN");

        em.getTransaction().begin();
        repo.dodaj(wolumin);
        em.getTransaction().commit();

        em.getTransaction().begin();
        wolumin.setJezyk("Niemiecki");
        repo.update(wolumin);
        em.getTransaction().commit();

        Wolumin updated = repo.znajdzPoId(wolumin.getId());
        assertEquals("Niemiecki", updated.getJezyk());
    }

    @Test
    void testUsun() {
        Wolumin wolumin = new Wolumin("Wydawnictwo GHI", "Hiszpański", "Tytuł OPQ");

        em.getTransaction().begin();
        repo.dodaj(wolumin);
        em.getTransaction().commit();

        em.getTransaction().begin();
        repo.usun(wolumin);
        em.getTransaction().commit();

        Wolumin retrieved = repo.znajdzPoId(wolumin.getId());
        assertNull(retrieved);
    }

    @Test
    void testZnajdzIZamknijPoId() {
        Wolumin wolumin = new Wolumin("Wydawnictwo JKL", "Francuski", "Tytuł RST");

        em.getTransaction().begin();
        repo.dodaj(wolumin);
        em.getTransaction().commit();

        em.getTransaction().begin();
        Wolumin locked = repo.znajdzIZamknijPoId(wolumin.getId());

        assertNotNull(locked);
        em.getTransaction().commit();
    }
    @Test
    public void testOptimisticLocking() {
        Wolumin wolumin = new Wolumin("Wydawnictwo GHI", "Hiszpański", "Tytuł OPQ");

        EntityTransaction tx1 = em.getTransaction();
        tx1.begin();
        em.persist(wolumin);
        tx1.commit();

        assertEquals(0L, wolumin.getVersion());

        EntityManager emA = emf.createEntityManager();
        EntityTransaction txA = emA.getTransaction();
        txA.begin();
        Wolumin woluminA = emA.find(Wolumin.class, wolumin.getId());

        woluminA.setTytul("Tytuł Zmieniony przez A");

        EntityManager emB = emf.createEntityManager();
        EntityTransaction txB = emB.getTransaction();
        txB.begin();
        Wolumin woluminB = emB.find(Wolumin.class, wolumin.getId());

        woluminB.setTytul("Tytuł Zmieniony przez B");

        txA.commit();
        emA.close();

        assertEquals(1L, woluminA.getVersion());

        RollbackException thrown = assertThrows(RollbackException.class, txB::commit);
        assertTrue(thrown.getCause() instanceof OptimisticLockException);
        emB.close();
    }

}
