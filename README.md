# RecipeBox
Projekt wykonany na potrzeby przedmiotu Aplikacje Mobilne.

Przygotował: `Julian Machowski`

Klasa: `4TP`

Rok szkolny: `2025/26`

## Kluczowe funkcje
*   **Wyszukiwanie online**: Integracja z API Spoonacular (tysiące przepisów).
*   **Własne przepisy**: Pełny system CRUD (dodawanie, edytowanie, usuwanie) lokalnych przepisów.
*   **Skalowanie porcji**: Inteligentne przeliczanie składników dla wybranej liczby osób.
*   **Listy zakupów**: Automatyczne generowanie list zakupów na podstawie przepisów.
*   **Kuchenny konwerter**: Narzędzie do przeliczania jednostek miar i wag.
*   **Tryb Ciemny**: Pełne wsparcie dla Dark Mode.

## Technologie i Architektura
Aplikacja została zbudowana zgodnie z najnowszymi standardami programowania na Androida:
*   **Architektura**: Clean Architecture (Domain, Data, UI) + MVVM.
*   **Baza danych**: Room (SQLite) - przechowywanie danych offline.
*   **Sieć**: Retrofit 2 + OkHttp - komunikacja z API.
*   **Nawigacja**: Jetpack Navigation Component + Safe Args.
*   **Obrazy**: Glide - szybkie ładowanie zdjęć.
*   **UI**: Material Design 3 + ViewBinding.

## Testowanie
Projekt posiada rozbudowaną warstwę testową:
*   **Testy jednostkowe**: JUnit 5 + Mockito (testy logiki biznesowej i ViewModeli).
*   **Testy UI**: Espresso.

# Pełna dokumentacja znajduje się w repozytorium
   
