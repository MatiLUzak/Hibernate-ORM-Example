package org.example.model;

import jakarta.persistence.*;
import org.example.exceptions.WypozyczajacyException;

@Entity
public class Nauczyciel extends TypWypozyczajacy {
    @Column(name="tytl", nullable=false)
    private String tytul;

    public Nauczyciel() {

    }
    public Nauczyciel(double kara, int maxDlWypoz, int maksLKsiazek, String tytul) {
        super(kara, maxDlWypoz, maksLKsiazek);
        if (tytul == null || tytul.isEmpty()) {
            throw new WypozyczajacyException("Błędny tytuł");
        }
        this.tytul = tytul;
    }

    public String getTytul() {
        return tytul;
    }

    public void setTytul(String tytul) {
        if (tytul == null || tytul.isEmpty()) {
            throw new WypozyczajacyException("Błędny tytuł");
        }
        this.tytul = tytul;
    }

    public String pobierzInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Kara: ").append(getKara()).append("\n");
        info.append("Max długość wypożyczenia: ").append(getMaxDlWypoz()).append("\n");
        info.append("Maksymalna liczba książek: ").append(getMaksLKsiazek()).append("\n");
        info.append("Tytuł: ").append(getTytul()).append("\n");
        return info.toString();
    }
}
