# NexusCart - Sklep Internetowy API & Frontend

Kompleksowy projekt sklepu internetowego zbudowany w oparciu o architekturę mikro-usługową (monolit z podziałem warstwowym) z nowoczesnym frontendem w React.

## 🚀 Technologie

### Backend
- **Java 17** + **Spring Boot 4.0.6**
- **Spring Data JPA**: Komunikacja z bazą danych (H2 In-Memory / PostgreSQL)
- **Spring Security**: Autoryzacja i zabezpieczenie endpointów (Basic Auth)
- **Spring Events**: Asynchroniczna obsługa zdarzeń
- **Lombok**: Redukcja kodu boilerplate
- **JUnit 5 & Mockito**: Testy jednostkowe

### Frontend
- **React 18** (z wykorzystaniem Hooków: `useState`, `useEffect`)
- **Vite**: Ultra-szybki build tool
- **Vanilla CSS**: Nowoczesny design z systemem zmiennych (CSS Variables)

## 🏗️ Architektura i Funkcjonalności

### 1. Struktura Warstwowa (Clean Code)
Projekt został podzielony na wyraźne warstwy, co ułatwia testowanie i utrzymanie kodu:
- **Controller**: Endpointy REST API.
- **Service**: Logika biznesowa i transakcje.
- **Repository**: Interfejsy do komunikacji z bazą danych.
- **DTO (Data Transfer Objects)**: Odseparowanie modeli bazodanowych od danych przesyłanych przez API.
- **Mappers**: Klasy odpowiedzialne za bezpieczne mapowanie między modelami a DTO.

### 2. Bezpieczeństwo (Security)
Zastosowano **Spring Security** z konfiguracją **Basic Auth**. Endpointy GET dla produktów są publiczne, natomiast system jest przygotowany pod pełne zabezpieczenie pozostałych operacji. Wyłączono CSRF oraz skonfigurowano CORS dla poprawnej współpracy z frontendem.

### 3. Asynchroniczne Zdarzenia (Events)
Wprowadzono system zdarzeń (**Spring Application Events**):
- Po złożeniu zamówienia przez klienta, system publikuje `OrderPlacedEvent`.
- `OrderEventListener` przechwytuje to zdarzenie asynchronicznie (`@Async`) i symuluje wysyłkę powiadomienia e-mail w osobnym wątku, nie opóźniając procesu składania zamówienia.

### 4. Obsługa Błędów
Globalny system obsługi wyjątków (**`GlobalExceptionHandler`**) zwraca czytelne komunikaty JSON w przypadku:
- Nieodnalezienia zasobu (404),
- Błędnych danych wejściowych (400),
- Błędów serwera (500).

### 5. Walidacja Danych
Wykorzystano **Jakarta Validation**. Każde żądanie POST/PUT jest walidowane pod kątem poprawności:
- Nazwa produktu nie może być pusta.
- Cena musi być większa od zera.
- Stan magazynowy nie może być ujemny.

## 🖥️ Jak uruchomić?

### Backend
1. Upewnij się, że masz zainstalowane Java 17.
2. Uruchom polecenie:
   ```bash
   mvn spring-boot:run
   ```
*API będzie dostępne pod adresem: `http://localhost:8080/api`*

### Frontend
1. Wejdź do folderu `frontend`.
2. Zainstaluj zależności: `npm install`.
3. Uruchom projekt:
   ```bash
   npm run dev
   ```
*Aplikacja będzie dostępna pod adresem: `http://localhost:5173/`*

## 🧪 Testy
Uruchomienie wszystkich testów jednostkowych (Mockito):
```bash
mvn test
```

## 🎥 Demo
Projekt zawiera zintegrowany plik `import.sql`, dzięki czemu po uruchomieniu baza danych jest od razu wypełniona przykładowymi produktami (iPhone 15, MacBook itp.), gotowymi do pokazania w prezentacji wideo.
