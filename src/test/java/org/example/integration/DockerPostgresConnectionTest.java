package org.example.integration;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DockerPostgresConnectionTest {

    private static EntityManagerFactory emf;
    private static EntityManager em;

    @BeforeAll
    static void beforeAll() {
        // Test
        emf = Persistence.createEntityManagerFactory("POSTGRES_RENT_PU");
        em = emf.createEntityManager();
    }

    @AfterAll
    static void afterAll() {
        // Zamykamy EntityManager i EntityManagerFactory po zakończeniu testów
        if (em != null) {
            em.close();
        }
        if (emf != null) {
            emf.close();
        }
    }

    @Test
    void testEntityManagerConnection() {
        // Sprawdzamy, czy EntityManager został poprawnie utworzony (czyli połączenie z bazą jest aktywne)
        assertNotNull(em, "EntityManager should not be null, indicating a successful connection to the database");
    }
}
