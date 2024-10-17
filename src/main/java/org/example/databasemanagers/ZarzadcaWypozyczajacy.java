package org.example.databasemanagers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PessimisticLockException;
import org.example.databaserepository.WypozyczajacyDatabaseRepository;
import org.example.exceptions.RepozytoriumException;
import org.example.model.Wypozyczajacy;

import java.util.UUID;

public class ZarzadcaWypozyczajacy {

    private final WypozyczajacyDatabaseRepository repozytorium;
    private final EntityManager em;

    public ZarzadcaWypozyczajacy(WypozyczajacyDatabaseRepository repozytorium, EntityManager em) {
        this.repozytorium = repozytorium;
        this.em = em;
    }

    public void dodajWypozyczajacy(Wypozyczajacy wypozyczajacy) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            repozytorium.dodaj(wypozyczajacy);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new RepozytoriumException("Błąd podczas dodawania wypożyczającego", e);
        }
    }

    public void usunWypozyczajacy(UUID id) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            Wypozyczajacy wypozyczajacy = repozytorium.znajdzIZamknijPoId(id);
            if (wypozyczajacy == null) {
                transaction.rollback();
                throw new RepozytoriumException("Nie znaleziono wypożyczającego");
            }
            repozytorium.usun(wypozyczajacy);
            transaction.commit();
        } catch (PessimisticLockException ple) {
            transaction.rollback();
            throw new RepozytoriumException("Nie można uzyskać blokady na wypożyczającego", ple);
        } catch (RepozytoriumException e) {
            throw e;
        } catch (Exception e) {
            transaction.rollback();
            throw new RepozytoriumException("Błąd podczas usuwania wypożyczającego", e);
        }
    }

    public Wypozyczajacy znajdzWypozyczajacy(UUID id) {
        try {
            return repozytorium.znajdzPoId(id);
        } catch (Exception e) {
            throw new RepozytoriumException("Błąd podczas wyszukiwania wypożyczającego", e);
        }
    }
}
