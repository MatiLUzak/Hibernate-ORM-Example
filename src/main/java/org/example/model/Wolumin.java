package org.example.model;

import org.example.excpetions.WoluminException;

public class Wolumin {
    private String wydawnictwo;
    private String jezyk;
    private String tytul;

    public Wolumin(String wydawnictwo, String jezyk, String tytul) {
        if(wydawnictwo.isEmpty()){
            throw new WoluminException("Błędne Wydawnictwo");
        }
        if(jezyk.isEmpty()){
            throw new WoluminException("Błędny język");
        }
        if(tytul.isEmpty()){
            throw new WoluminException("Błędny tytuł");
        }
        this.wydawnictwo = wydawnictwo;
        this.jezyk = jezyk;
        this.tytul = tytul;
    }

    public String getWydawnictwo() {
        return wydawnictwo;
    }

    public String getJezyk() {
        return jezyk;
    }

    public String getTytul() {
        return tytul;
    }

    public void setWydawnictwo(String wydawnictwo) {
        this.wydawnictwo = wydawnictwo;
    }

    public void setJezyk(String jezyk) {
        this.jezyk = jezyk;
    }

    public void setTytul(String tytul) {
        this.tytul = tytul;
    }
}
