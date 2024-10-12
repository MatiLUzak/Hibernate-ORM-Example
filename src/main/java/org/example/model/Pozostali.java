package org.example.model;

import org.example.exceptions.WypozyczajacyException;

public class Pozostali extends TypWypozyczajacy {
    private String zawod;

    // Konstruktor z walidacją
    public Pozostali(double kara, int maxDlWypoz, int maksLKsiazek, String zawod) {
        super(kara, maxDlWypoz, maksLKsiazek);
        if (zawod == null || zawod.isEmpty()) {
            throw new WypozyczajacyException("Błędny zawód");
        }
        this.zawod = zawod;
    }

    // Getter dla zawod
    public String getZawod() {
        return zawod;
    }

    // Setter z walidacją
    public void setZawod(String zawod) {
        if (zawod == null || zawod.isEmpty()) {
            throw new WypozyczajacyException("Błędny zawód");
        }
        this.zawod = zawod;
    }

    // Metoda do wyświetlania informacji o Typie
    public String pobierzInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Kara: ").append(getKara()).append("\n");
        info.append("Max długość wypożyczenia: ").append(getMaxDlWypoz()).append("\n");
        info.append("Maksymalna liczba książek: ").append(getMaksLKsiazek()).append("\n");
        info.append("Zawód: ").append(getZawod()).append("\n");
        return info.toString();
    }
}
