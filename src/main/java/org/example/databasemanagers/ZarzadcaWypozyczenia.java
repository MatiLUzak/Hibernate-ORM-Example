package org.example.databasemanagers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PessimisticLockException;
import org.example.databaserepository.WypozyczenieDatabaseRepository;
import org.example.exceptions.RepozytoriumException;
import org.example.model.Wypozyczenie;

import java.util.UUID;

public class ZarzadcaWypozyczenia {

    private final WypozyczenieDatabaseRepository repozytorium;
    private final EntityManager em;

    public ZarzadcaWypozyczenia(WypozyczenieDatabaseRepository repozytorium, EntityManager em) {
        this.repozytorium = repozytorium;
        this.em = em;
    }

    public void dodajWypozyczenie(Wypozyczenie wypozyczenie) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            repozytorium.dodaj(wypozyczenie);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new RepozytoriumException("Błąd podczas dodawania wypożyczenia", e);
        }
    }

    public void usunWypozyczenie(UUID id) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            Wypozyczenie wypozyczenie = repozytorium.znajdzIZamknijPoId(id);
            if (wypozyczenie == null) {
                transaction.rollback();
                throw new RepozytoriumException("Nie znaleziono wypożyczenia");
            }
            repozytorium.usun(wypozyczenie); // Usuwa wypożyczenie z bazy danych
            transaction.commit();
        } catch (PessimisticLockException ple) {
            transaction.rollback();
            throw new RepozytoriumException("Nie można uzyskać blokady na wypożyczenie", ple);
        } catch (RepozytoriumException e) {
            // Rzucamy ponownie, aby zachować oryginalny komunikat
            throw e;
        } catch (Exception e) {
            transaction.rollback();
            throw new RepozytoriumException("Błąd podczas usuwania wypożyczenia", e);
        }
    }




    public double obliczKare(UUID id) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            Wypozyczenie wypozyczenie = repozytorium.znajdzPoId(id);
            if (wypozyczenie == null) {
                throw new RepozytoriumException("Nie znaleziono wypożyczenia");
            }
            double kara = wypozyczenie.obliczKare();
            transaction.commit();
            return kara;
        } catch (Exception e) {
            transaction.rollback();
            throw new RepozytoriumException("Błąd podczas obliczania kary", e);
        }
    }
}
