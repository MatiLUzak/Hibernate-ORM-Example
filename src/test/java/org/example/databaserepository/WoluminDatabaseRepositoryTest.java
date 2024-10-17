package org.example.databaserepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
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

        // Clean up data before each test
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
        // Arrange
        Wolumin wolumin = new Wolumin("Wydawnictwo ABC", "Polski", "Tytuł XYZ");

        // Act
        em.getTransaction().begin();
        repo.dodaj(wolumin);
        em.getTransaction().commit();

        // Assert
        Wolumin retrieved = repo.znajdzPoId(wolumin.getId());
        assertNotNull(retrieved);
        assertEquals(wolumin.getId(), retrieved.getId());
    }

    @Test
    void testUpdate() {
        // Arrange
        Wolumin wolumin = new Wolumin("Wydawnictwo DEF", "Angielski", "Tytuł LMN");

        em.getTransaction().begin();
        repo.dodaj(wolumin);
        em.getTransaction().commit();

        // Act
        em.getTransaction().begin();
        wolumin.setJezyk("Niemiecki");
        repo.update(wolumin);
        em.getTransaction().commit();

        // Assert
        Wolumin updated = repo.znajdzPoId(wolumin.getId());
        assertEquals("Niemiecki", updated.getJezyk());
    }

    @Test
    void testUsun() {
        // Arrange
        Wolumin wolumin = new Wolumin("Wydawnictwo GHI", "Hiszpański", "Tytuł OPQ");

        em.getTransaction().begin();
        repo.dodaj(wolumin);
        em.getTransaction().commit();

        // Act
        em.getTransaction().begin();
        repo.usun(wolumin);
        em.getTransaction().commit();

        // Assert
        Wolumin retrieved = repo.znajdzPoId(wolumin.getId());
        assertNull(retrieved);
    }

    @Test
    void testZnajdzIZamknijPoId() {
        // Arrange
        Wolumin wolumin = new Wolumin("Wydawnictwo JKL", "Francuski", "Tytuł RST");

        em.getTransaction().begin();
        repo.dodaj(wolumin);
        em.getTransaction().commit();

        // Act
        em.getTransaction().begin();
        Wolumin locked = repo.znajdzIZamknijPoId(wolumin.getId());

        // Assert
        assertNotNull(locked);
        em.getTransaction().commit();
    }
}
