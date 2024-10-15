package org.example.model;

import org.example.exceptions.WoluminException;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Ksiazka extends Wolumin{

    @ElementCollection
    @CollectionTable(name = "ksiazka_autor", joinColumns = @JoinColumn(name = "ksiazka_id"))
    @Column(name = "autor")
    private List<String> autor;

    public Ksiazka() {}


    public Ksiazka(String wydawnictwo, String jezyk, String tytul, List<String> autor) {
        super(wydawnictwo, jezyk, tytul);
        if(autor==null||autor.isEmpty()){
            throw new WoluminException("Brak autora książki");
        }
        this.autor = autor;

    }

    public List<String> getAutor() {
        return autor;
    }

    public void setAutor(List<String> autor) {
        if(autor==null||autor.isEmpty()){
            throw new WoluminException("Brak autora książki");
        }
        this.autor = autor;
    }
}
