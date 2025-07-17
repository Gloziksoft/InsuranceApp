InsuranceApp
A web application for insurance management written in Java, using Spring Boot, Thymeleaf, and Bootstrap.

⚙️ How to Run the Project
1. Start the Database with Docker
Run the following command to start a MySQL database container:

bash
Kopírovať
Upraviť
docker-compose up -d
Make sure Docker is installed and running.

The database will be initialized with the required schema and data automatically (see docker-compose.yml for details).

2. Run the Application in IntelliJ IDEA
Open the project in IntelliJ IDEA.

Make sure the correct JDK and Maven are configured.

Run the application using the Spring Boot main class (PoistenieAppApplication.java).

The app will be available at:

arduino
Kopírovať
Upraviť
http://localhost:8080
👥 User Roles and Registration
Admin can add and manage insured persons manually.

New users can register themselves via the registration form in the application.

🔐 Login Credentials for Testing
Admin account

Email: admin@test.com

Password: 123

User account

Email: user@test.com

Password: 123

Users created by admin
These users have access only to their own profiles:

martinnovak@test.com / 123

luciakovacova@test.com / 123

Passwords are currently shortened to 3 characters for easier testing.

PoistenieApp
Webová aplikácia na správu poistenia, vytvorená v Jave pomocou Spring Boot, Thymeleaf a Bootstrapu.

⚙️ Ako spustiť projekt
1. Spustenie databázy cez Docker
Spusť databázu pomocou Dockeru:

bash
Kopírovať
Upraviť
docker-compose up -d
Uisti sa, že máš nainštalovaný a spustený Docker.

Databáza sa automaticky inicializuje pomocou definícií v docker-compose.yml.

2. Spustenie aplikácie v IntelliJ IDEA
Otvor projekt v IntelliJ IDEA.

Skontroluj, že máš nastavený správny JDK a Maven.

Spusť aplikáciu cez hlavnú Spring Boot triedu (PoistenieAppApplication.java).

Aplikácia bude dostupná na adrese:

arduino
Kopírovať
Upraviť
http://localhost:8080
👥 Používateľské roly a registrácia
Admin môže pridávať a spravovať poistencov manuálne.

Nový používateľ sa môže zaregistrovať cez formulár v aplikácii.

🔐 Prihlasovacie údaje na testovanie
Admin účet

Email: admin@test.com

Heslo: 123

Používateľský účet

Email: user@test.com

Heslo: 123

Používatelia vytvorení adminom
Majú prístup len k svojmu vlastnému profilu:

martinnovak@test.com / 123

luciakovacova@test.com / 123
