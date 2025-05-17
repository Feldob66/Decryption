# 🎮 Decryption – Szóalapú logikai játék rendszerterve

## 🧩 Projekt leírása

A **Decryption** egy Wordle-szerű, intellektuális, logikai alapú szókereső játék, ahol a játékosoknak ki kell találniuk a rejtett szót korlátozott számú próbálkozással. A játék alapötlete a 2024-ben indult "Terminal" nevű P2E GameFi projektből származik, amely a kriptovalutás világban vált népszerűvé. Jelen projektünk célja, hogy egy asztali Java-alkalmazás formájában újraalkossuk a játékélményt.

## 👨‍💻 Fejlesztői csapat

* Páros projekt
* Verziókezelés: Git (GitHub)
* Projektmenedzsment: Kanban (Trello / GitHub Projects)

## 🏗️ Architektúra

* Technológia: Java 17+ és JavaFX
* Tervezési minta: MVC (Model-View-Controller)
* SOLID elvek alkalmazása, főként:

  * **SRP** (Single Responsibility Principle): minden osztály egyértelmű feladatot lát el (pl. GameController a UI eseményekért, GameService a játékmenet logikájáért)
  * **OCP** (Open/Closed Principle): bővíthetőség stratégiák, pontozás vagy szabályrendszerek cseréjével anélkül, hogy meglévő kódot módosítani kellene

## 🔑 Játékmenet

* A játékosnak naponta X számú próbálkozása van.
* Minden fordulóban 7-10 szóból kell kiválasztani a helyeset.
* Minden szóválasztás után a rendszer visszajelzést ad: hány karakter van **jó helyen** (pl. 3/9).
* 4 próbálkozás alatt kell eltalálni a helyes szót.

  * 1. próbálkozás: 200 pont
  * 2. próbálkozás: 150 pont
  * 3. próbálkozás: 100 pont
  * 4. próbálkozás: 50 pont
  * 5. próbálkozás: veszteség

## 🧠 Alkalmazott tervezési minták

* **MVC (Model-View-Controller)**: a UI, üzleti logika és adatmodell elkülönítése
* **Observer**: eseménykezelés callback-ekkel, a nézet automatikusan frissül az állapotváltozásokra
* **Strategy**: különböző pontszámítási és szóválasztási algoritmusok könnyű cserélhetősége

## 📋 Modulok és osztályok

### `GameController` (SRP)

* Feladata: a felhasználói interakciók kezelése, a játékmenet indítása és vezérlése
* Kommunikál a `GameService` és `GameView` között

### `GameService` (SRP + OCP)

* Tartalmazza a játékmenet szabályait és logikáját
* Szavak kezelése, pontszámítás, visszajelzések
* Bővíthető új szabályrendszerekkel (pl. pontozási stratégia váltásával)

### `WordListProvider`

* Felelős a napi szólisták kezeléséért és generálásáért

### `ScoreManager`

* Játékos pontszámának kezelése és tárolása

### `Logger`

* Alkalmazás naplózása fájlba
* Hibák és események követése

## 📌 UI terv (JavaFX)

* **Főképernyő**: új játék indítása, statisztikák
* **Játéktábla**: szólista megjelenítése, visszajelzések (pl. hány karakter található jó helyen)
* **Visszajelzési ablak**: pontszám és játék eredményének megjelenítése
* **Admin felület**: napi szólista frissítése és statisztikák (opcionális)

## 🪵 Naplózás

* Napló fájlba (`/logs/game.log`)
* Mentett események: próbálkozások száma, hibák, nyertes játékok
* Fejlesztői napló (külön fájl): ki mikor mit csinált (`dev-log.md`)

## 📘 Fejlesztési ütemterv

### Fázis 1 – Alapverzió

* [x] Projekt inicializálása
* [x] Git repo és Kanban beállítása
* [x] Alap MVC struktúra

### Fázis 2 – Játékmenet és logika

* [x] Szólista generátor és alap UI elkészítése
* [x] Pontszámítás implementálása
* [x] Helyes karakterek visszajelzése
* [x] Observer és Strategy minták alkalmazása

### Fázis 3 – UI fejlesztés és logolás

* [ ] UI styling (JavaFX CSS)
* [ ] Logolás fejlesztése

### Fázis 4 – Tesztelés és dokumentáció

* [ ] JUnit tesztek írása
* [ ] README és rendszerterv véglegesítése

## 🧪 Bővítési lehetőségek

* Hálózatos (LAN) többjátékos mód
* Highscore táblázat
* Extra játékmódok: véletlenszerű karaktercsere, időkorlátos játék