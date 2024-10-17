package org.example.model;

import org.example.exceptions.WypozyczajacyException;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Wypozyczajacy extends AbstractEntity {


    @ManyToOne
    @JoinColumn(name = "typ_wypozyczajacy_id", nullable = false)
    private TypWypozyczajacy typWypozyczajacy;

    @Column(nullable = false)
    private String nazwa;

    private LocalDate dataUr;

    @Column(nullable = false)
    private String adres;

    public Wypozyczajacy() {}

    public Wypozyczajacy(TypWypozyczajacy typWypozyczajacy, String nazwa, LocalDate dataUr, String adres) {
        if (typWypozyczajacy == null) {
            throw new WypozyczajacyException("Błędny typWypozyczajacy");
        }
        if (nazwa == null || nazwa.isEmpty()) {
            throw new WypozyczajacyException("Błędna nazwa");
        }
        if (adres == null || adres.isEmpty()) {
            throw new WypozyczajacyException("Błędny adres");
        }

        this.typWypozyczajacy = typWypozyczajacy;
        this.nazwa = nazwa;
        this.dataUr = dataUr;
        this.adres = adres;
        //this.uuid = UUID.randomUUID();
    }


    public TypWypozyczajacy getTypWypozyczajacy() {
        return typWypozyczajacy;
    }

    public String getNazwa() {
        return nazwa;
    }

    public LocalDate getDataUr() {
        return dataUr;
    }

    public String getAdres() {
        return adres;
    }

    public void setTypWypozyczajacy(TypWypozyczajacy typWypozyczajacy) {
        if (typWypozyczajacy == null) {
            throw new WypozyczajacyException("Błędny typWypozyczajacy");
        }
        this.typWypozyczajacy = typWypozyczajacy;
    }

    public void setNazwa(String nazwa) {
        if (nazwa == null || nazwa.isEmpty()) {
            throw new WypozyczajacyException("Błędna nazwa");
        }
        this.nazwa = nazwa;
    }

    public void setDataUr(LocalDate dataUr) {
        this.dataUr = dataUr;
    }

    public void setAdres(String adres) {
        if (adres == null || adres.isEmpty()) {
            throw new WypozyczajacyException("Błędny adres");
        }
        this.adres = adres;
    }

    public String pobierzInformacjeWypozyczajacego() {
        StringBuilder info = new StringBuilder();
        info.append("Nazwa: ").append(getNazwa()).append("\n");
        info.append("Data urodzenia: ").append(getDataUr()).append("\n");
        info.append("Adres: ").append(getAdres()).append("\n");
        if (typWypozyczajacy != null) {
            info.append("Typ wypożyczającego: ").append(typWypozyczajacy.pobierzInfo()).append("\n");
        }
        return info.toString();
    }
}
