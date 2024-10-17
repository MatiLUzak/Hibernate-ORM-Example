package org.example.databasemanagers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PessimisticLockException;
import org.example.databaserepository.WoluminDatabaseRepository;
import org.example.exceptions.RepozytoriumException;
import org.example.model.Wolumin;

import java.util.UUID;

public class ZarzadcaWoluminu {

    private final WoluminDatabaseRepository repozytorium;
    private final EntityManager em;

    public ZarzadcaWoluminu(WoluminDatabaseRepository repozytorium, EntityManager em) {
        this.repozytorium = repozytorium;
        this.em = em;
    }

    public void dodajWolumin(Wolumin wolumin) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            repozytorium.dodaj(wolumin);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new RepozytoriumException("Błąd podczas dodawania woluminu", e);
        }
    }

    public void usunWolumin(UUID id) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            Wolumin wolumin = repozytorium.znajdzIZamknijPoId(id);
            if (wolumin == null) {
                transaction.rollback();
                throw new RepozytoriumException("Nie znaleziono woluminu");
            }
            repozytorium.usun(wolumin);
            transaction.commit();
        } catch (PessimisticLockException ple) {
            transaction.rollback();
            throw new RepozytoriumException("Nie można uzyskać blokady na wolumin", ple);
        } catch (RepozytoriumException e) {
            throw e;
        } catch (Exception e) {
            transaction.rollback();
            throw new RepozytoriumException("Błąd podczas usuwania woluminu", e);
        }
    }

    public Wolumin znajdzWolumin(UUID id) {
        try {
            return repozytorium.znajdzPoId(id);
        } catch (Exception e) {
            throw new RepozytoriumException("Błąd podczas wyszukiwania woluminu", e);
        }
    }
}
