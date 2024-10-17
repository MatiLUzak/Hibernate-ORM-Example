package org.example.databaserepository;

import jakarta.persistence.EntityManager;

public abstract class DatabaseRepository<T, ID> {

    protected final EntityManager em;
    private final Class<T> entityClass;

    public DatabaseRepository(EntityManager em, Class<T> entityClass) {
        this.em = em;
        this.entityClass = entityClass;
    }

    public void dodaj(T entity) {
        em.persist(entity);
    }

    public T znajdzPoId(ID id) {
        return em.find(entityClass, id);
    }

    public void update(T entity) {
        em.merge(entity);
    }

    public void usun(T entity) {
        T attached = em.merge(entity);
        em.remove(attached);
    }
}
