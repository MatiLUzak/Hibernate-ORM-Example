package org.example.model;

import org.example.exceptions.WypozyczenieException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WypozyczenieTest {

    @Test
    void wypozyczenieTest() {
        List<String> autorzy = Arrays.asList("J.K. Rowling");
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(new Nauczyciel(0.0, 30, 10, "Dr"), "Jan Kowalski", LocalDate.of(2023, 5, 1), "Warszawa");
        Wolumin wolumin = new Beletrystyka("Helion", "PL", "Harry Potter", autorzy, "15+", "Fantasy");

        Wypozyczenie wypozyczenie = new Wypozyczenie(wypozyczajacy, wolumin);

        assertNotNull(wypozyczenie.getDataOd());
        assertNull(wypozyczenie.getDataDo());

        wypozyczenie.koniecWypozyczenia();
        assertNotNull(wypozyczenie.getDataDo());

        //assertNotNull(wypozyczenie.getUuid());

        Wypozyczajacy nowyWypozyczajacy = new Wypozyczajacy(new Nauczyciel(0.0, 30, 10, "Prof"), "Adam Nowak", LocalDate.of(2023, 5, 1), "Kraków");
        wypozyczenie.setWypozyczajacy(nowyWypozyczajacy);
        assertEquals(nowyWypozyczajacy, wypozyczenie.getWypozyczajacy());

        Wolumin nowyWolumin = new Beletrystyka("Helion", "PL", "Lord of the Rings", autorzy, "15+", "Fantasy");
        wypozyczenie.setWolumin(nowyWolumin);
        assertEquals(nowyWolumin, wypozyczenie.getWolumin());

        assertEquals(0, wypozyczenie.dlugoscWypozyczenia());
        assertEquals(0, wypozyczenie.obliczKare());
    }

    @Test
    void wypozyczenieExceptionsTest() {
        Wypozyczajacy wypozyczajacy = new Wypozyczajacy(new Nauczyciel(0.0, 30, 10, "Dr"), "Jan Kowalski", LocalDate.of(2023, 5, 1), "Warszawa");
        List<String> autorzy = Arrays.asList("J.K. Rowling");
        Wolumin wolumin = new Beletrystyka("Helion", "PL", "Harry Potter", autorzy, "15+", "Fantasy");

        // Testowanie wyjątków w konstruktorze
        assertThrows(WypozyczenieException.class, () -> new Wypozyczenie(null, wolumin));
        assertThrows(WypozyczenieException.class, () -> new Wypozyczenie(wypozyczajacy, null));

        Wypozyczenie wypozyczenie = new Wypozyczenie(wypozyczajacy, wolumin);

        // Testowanie wyjątków w setterach
        assertThrows(WypozyczenieException.class, () -> wypozyczenie.setWypozyczajacy(null));
        assertThrows(WypozyczenieException.class, () -> wypozyczenie.setWolumin(null));
    }
}
