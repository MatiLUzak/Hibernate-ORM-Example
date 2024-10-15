package org.example.model;

import jakarta.persistence.*;
import org.example.exceptions.WypozyczajacyException;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class TypWypozyczajacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="kara",nullable = false)
    private double kara;
    @Column(name="max_dl_wypoz",nullable = false)
    private int maxDlWypoz;
    @Column(name="maks_l_ksiazek",nullable = false)
    private int maksLKsiazek;

    public TypWypozyczajacy() {
    }

    public TypWypozyczajacy(double kara, int maxDlWypoz, int maksLKsiazek) {
        if(kara <0){
            throw new WypozyczajacyException("Kara nie może być ujemna");
        }
        if(maxDlWypoz < 0){
            throw new WypozyczajacyException("MaxDlWypoz nie może być ujemn");
        }
        if(maksLKsiazek < 0){
            throw new WypozyczajacyException("Maksymalna liczba książek nie może być ujemna");
        }
        this.kara = kara;
        this.maxDlWypoz = maxDlWypoz;
        this.maksLKsiazek = maksLKsiazek;
    }

    public double getKara() {
        return kara;
    }

    public int getMaxDlWypoz() {
        return maxDlWypoz;
    }

    public int getMaksLKsiazek() {
        return maksLKsiazek;
    }
    public void setKara(double kara) {
        if (kara < 0) {
            throw new WypozyczajacyException("Kara nie może być ujemna");
        }
        this.kara = kara;
    }

    public void setMaxDlWypoz(int maxDlWypoz) {
        if (maxDlWypoz < 0) {
            throw new WypozyczajacyException("MaxDlWypoz nie może być ujemne");
        }
        this.maxDlWypoz = maxDlWypoz;
    }

    public void setMaksLKsiazek(int maksLKsiazek) {
        if (maksLKsiazek < 0) {
            throw new WypozyczajacyException("Maksymalna liczba książek nie może być ujemna");
        }
        this.maksLKsiazek = maksLKsiazek;
    }

    public String pobierzInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Kara: ").append(getKara()).append("\n");
        info.append("Maksymalna długość wypożyczenia: ").append(getMaxDlWypoz()).append("\n");
        info.append("Maksymalna liczba książek: ").append(getMaksLKsiazek()).append("\n");
        return info.toString();
    }
}
