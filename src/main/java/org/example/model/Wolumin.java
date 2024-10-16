package org.example.model;

import org.example.exceptions.WoluminException;
import jakarta.persistence.*;
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Wolumin  extends AbstractEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wydawnictwo", nullable = false)
    private String wydawnictwo;

    @Column(name = "jezyk", nullable = false)
    private String jezyk;

    @Column(name = "tytul", nullable = false)
    private String tytul;

    public Wolumin() {}

    public Wolumin(String wydawnictwo, String jezyk, String tytul) {
        if(wydawnictwo==null||wydawnictwo.isEmpty()){
            throw new WoluminException("Błędne Wydawnictwo");
        }
        if(jezyk==null||jezyk.isEmpty()){
            throw new WoluminException("Błędny język");
        }
        if(tytul==null||tytul.isEmpty()){
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
        if(wydawnictwo==null||wydawnictwo.isEmpty()){
            throw new WoluminException("Błędne Wydawnictwo");
        }
        this.wydawnictwo = wydawnictwo;
    }

    public void setJezyk(String jezyk) {
        if(jezyk==null||jezyk.isEmpty()){
            throw new WoluminException("Błędny jezyk");
        }
        this.jezyk = jezyk;
    }

    public void setTytul(String tytul) {
        if(tytul==null||tytul.isEmpty()){
            throw new WoluminException("Błdny tytul");
        }
        this.tytul = tytul;
    }
}
