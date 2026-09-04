# Development Notes

Technical development notes documenting application changes,
debugging, implementation decisions, and development experiments.

These notes focus on application development rather than production
deployment and infrastructure.

---

## Development Log

### 2026-09-02 — Development Notes

Development notes will be used to document:

- Java / Spring Boot application changes
- debugging and problem solving
- REST API development
- validation and exception handling
- database-related changes
- DTO and entity design
- security implementation
- testing
- code refactoring
- development experiments
- technical decisions and lessons learned

These notes can later be used as a source for technical documentation
and LinkedIn posts.

---

## Development Testing

# Development Notes 2026-09-03

## Testing / Testovanie

Testing focuses on business logic and correct interaction between application components.
Testovanie je zamerané na business logiku a správnu spoluprácu jednotlivých častí aplikácie.

### Unit Tests / Jednotkové testy

JUnit is used for testing service-layer business logic.
JUnit sa používa na testovanie business logiky v service vrstve.

Mockito is used to isolate the tested component by mocking its dependencies.
Mockito sa používa na izolovanie testovanej komponenty pomocou mockovania jej závislostí.

Tests cover successful scenarios as well as expected error cases.
Testy pokrývajú úspešné scenáre aj očakávané chybové stavy.

### Integration Tests / Integračné testy

Integration tests verify the interaction between multiple application layers.
Integračné testy overujú spoluprácu viacerých vrstiev aplikácie.

They also verify communication with the database and persistence layer.
Zároveň overujú komunikáciu s databázou a persistence vrstvou.

The goal is to verify that the main application flow works correctly as a whole.
Cieľom je overiť, že hlavný aplikačný flow funguje správne ako celok.

### Testing Approach / Prístup k testovaniu

Unit tests provide fast feedback during development.
Unit tests poskytujú rýchlu spätnú väzbu počas vývoja.

Integration tests verify that the application works correctly with its real dependencies.
Integration tests overujú, že aplikácia správne funguje so svojimi reálnymi závislosťami.

Tests focus on application behaviour rather than implementation details.
Testy sa zameriavajú na správanie aplikácie, nie na detaily implementácie.

## Code Structure / Štruktúra kódu

The application is organized into clearly separated layers.
Aplikácia je organizovaná do jasne oddelených vrstiev.

Controllers handle HTTP requests.
Controllers spracúvajú HTTP požiadavky.

Services contain business logic.
Services obsahujú business logiku.

Repositories handle database access.
Repositories zabezpečujú prístup k databáze.

DTOs are used for communication between the API and application layers.
DTOs sa používajú na komunikáciu medzi API a aplikačnou vrstvou.

The structure is intentionally kept simple and consistent so that the purpose of individual classes is easy to understand.
Štruktúra je zámerne jednoduchá a konzistentná, aby bol účel jednotlivých tried ľahko pochopiteľný.

