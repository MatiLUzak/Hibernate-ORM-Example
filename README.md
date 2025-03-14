# Hibernate-ORM-Example

## 📌 Opis projektu

**Hibernate-ORM-Example** to aplikacja napisana w języku **Java**, która demonstruje wykorzystanie **Hibernate** do mapowania obiektowo-relacyjnego (ORM). Projekt ilustruje, jak za pomocą Hibernate można zarządzać operacjami na bazie danych w sposób obiektowy, upraszczając interakcję z relacyjnymi bazami danych.

## 🛠 Wymagania

Aby uruchomić projekt, potrzebujesz:

- **Java Development Kit (JDK) 11** lub nowszy
- **Maven** do zarządzania zależnościami i budowania projektu
- **Relacyjnej bazy danych** (np. MySQL, PostgreSQL) z odpowiednimi uprawnieniami dostępu

## 🚀 Instalacja i uruchomienie

1. **Klonowanie repozytorium:**

   ```bash
   git clone https://github.com/MatiLUzak/nbdhibernate.git
   cd nbdhibernate
   ```

2. **Konfiguracja bazy danych:**

   - Upewnij się, że masz zainstalowaną i uruchomioną relacyjną bazę danych.
   - Skonfiguruj połączenie z bazą danych w pliku `src/main/resources/hibernate.cfg.xml`, dostosowując parametry takie jak `hibernate.connection.url`, `hibernate.connection.username` i `hibernate.connection.password` do swojej konfiguracji.

3. **Budowanie projektu za pomocą Mavena:**

   ```bash
   mvn clean install
   ```

4. **Uruchomienie aplikacji:**

   - Uruchom główną klasę aplikacji znajdującą się w `src/main/java`.
   - Aplikacja wykona operacje na bazie danych zgodnie z implementacją.

## 📂 Struktura projektu

```
nbdhibernate/
├── .idea/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── [pakiet z kodem źródłowym]
│   │   └── resources/
│   │       └── hibernate.cfg.xml
│   └── test/
│       └── java/
├── .gitignore
├── pom.xml
└── README.md
```

- **`src/main/java/`** – zawiera kod źródłowy aplikacji.
- **`src/main/resources/`** – zawiera pliki konfiguracyjne, takie jak `hibernate.cfg.xml`.
- **`src/test/java/`** – zawiera testy jednostkowe.
- **`pom.xml`** – plik konfiguracyjny Mavena, definiujący zależności i pluginy.

## ✍️ Autor

- **MatiLUzak** – [Profil GitHub](https://github.com/MatiLUzak)

## 📜 Licencja

Ten projekt jest licencjonowany na podstawie licencji MIT. Szczegóły znajdują się w pliku `LICENSE`.
