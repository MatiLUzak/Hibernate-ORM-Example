package org.example.model;

import org.example.exceptions.WypozyczajacyException;
import jakarta.persistence.*;

@Entity
public class Uczen extends TypWypozyczajacy {
    @Column(name="nr_Semestru", nullable=false)
    private String nrSemestru;

    public Uczen() {

    }
    public Uczen(double kara, int maxDlWypoz, int maksLKsiazek, String nrSemestru) {
        super(kara, maxDlWypoz, maksLKsiazek);
        if (nrSemestru == null || nrSemestru.isEmpty()) {
            throw new WypozyczajacyException("Błędny nrSemestru");
        }
        this.nrSemestru = nrSemestru;
    }

    public String getNrSemestru() {
        return nrSemestru;
    }

    public void setNrSemestru(String nrSemestru) {
        if (nrSemestru == null || nrSemestru.isEmpty()) {
            throw new WypozyczajacyException("Błędny nrSemestru");
        }
        this.nrSemestru = nrSemestru;
    }

    public String pobierzInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Kara: ").append(getKara()).append("\n");
        info.append("Max długość wypożyczenia: ").append(getMaxDlWypoz()).append("\n");
        info.append("Maksymalna liczba książek: ").append(getMaksLKsiazek()).append("\n");
        info.append("Nr semestru: ").append(getNrSemestru()).append("\n");
        return info.toString();
    }
}
