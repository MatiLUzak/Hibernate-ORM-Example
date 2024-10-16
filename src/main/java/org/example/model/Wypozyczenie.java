package org.example.model;

import org.example.exceptions.WypozyczenieException;
import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Wypozyczenie extends AbstractEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID uuid;

    @ManyToOne // Relacja wiele do jednego z Wypozyczajacy
    @JoinColumn(name = "wypozyczajacy_id", nullable = false)
    private Wypozyczajacy wypozyczajacy;

    @ManyToOne // Relacja wiele do jednego z Wolumin
    @JoinColumn(name = "wolumin_id", nullable = false)
    private Wolumin wolumin;

    @Column(name = "data_od", nullable = false)
    private LocalDateTime dataOd;

    @Column(name = "data_do")
    private LocalDateTime dataDo;

    public Wypozyczenie() {
    }
    public Wypozyczenie(Wypozyczajacy wypozyczajacy, Wolumin wolumin) {
        if (wypozyczajacy == null) {
            throw new WypozyczenieException("Błędny wypożyczający");
        }
        if (wolumin == null) {
            throw new WypozyczenieException("Błędny wolumin");
        }
        this.wypozyczajacy = wypozyczajacy;
        this.wolumin = wolumin;
        this.dataOd = LocalDateTime.now();  // Ustawienie daty początkowej na teraz
        //this.uuid = UUID.randomUUID();  // Generowanie UUID
    }

    // Gettery
    public Wypozyczajacy getWypozyczajacy() {
        return wypozyczajacy;
    }

    public Wolumin getWolumin() {
        return wolumin;
    }

    public LocalDateTime getDataOd() {
        return dataOd;
    }

    public LocalDateTime getDataDo() {
        return dataDo;
    }

    public UUID getUuid() {
        return uuid;
    }

    // Settery z walidacją
    public void setWypozyczajacy(Wypozyczajacy wypozyczajacy) {
        if (wypozyczajacy == null) {
            throw new WypozyczenieException("Błędny wypożyczający");
        }
        this.wypozyczajacy = wypozyczajacy;
    }

    public void setWolumin(Wolumin wolumin) {
        if (wolumin == null) {
            throw new WypozyczenieException("Błędny wolumin");
        }
        this.wolumin = wolumin;
    }

    // Zakończenie wypożyczenia - ustawienie daty zwrotu
    public void koniecWypozyczenia() {
        this.dataDo = LocalDateTime.now();
    }

    // Obliczanie długości wypożyczenia w dniach
    public double dlugoscWypozyczenia() {
        if (dataDo == null) {
            return 0;
        }
        Duration duration = Duration.between(dataOd, dataDo);
        return (double) duration.toDays();
    }

    // Obliczanie kary na podstawie długości wypożyczenia
    public double obliczKare() {
        return wypozyczajacy.getTypWypozyczajacy().getKara() * dlugoscWypozyczenia();
    }
}
