# 🎮 Decryption – Szóalapú logikai játék rendszerterve

## 🧩 Projekt leírása

A **Decryption** egy Wordle-szerű, intellektuális, logikai alapú szókereső játék, ahol a játékosoknak ki kell találniuk a rejtett szót korlátozott számú próbálkozással. A játék alapötlete a 2024-ben indult "Terminal" nevű P2E GameFi projektből származik, amely a kriptovalutás világban vált népszerűvé. Jelen projektünk célja, hogy egy asztali Java-alkalmazás formájában újraalkossuk a játékélményt.

## 👨‍💻 Fejlesztői csapat

- Páros projekt
- Verziókezelés: Git (GitHub)
- Projektmenedzsment: Kanban (Trello / GitHub Projects)

## 🏗️ Architektúra

- Technológia: Java 17+ és JavaFX
- Tervezési minta: MVC (Model-View-Controller)
- Kódarchitektúra: SOLID elvek mentén, különös tekintettel az SRP és OCP betartására

## 🔑 Játékmenet

- A játékosnak naponta X számú próbálkozása van.
- Minden fordulóban 7-10 szóból kell kiválasztani a helyeset.
- Minden szóválasztás után a rendszer visszajelzést ad: hány karakter van **jó helyen** (pl. 3/9).
- 4 próbálkozás alatt kell eltalálni a helyes szót.
  - 1. próbálkozás: 200 pont
  - 2. próbálkozás: 150 pont
  - 3. próbálkozás: 100 pont
  - 4. próbálkozás: 50 pont
  - 5. próbálkozás: veszteség

## 🧠 Alkalmazott tervezési minták

- **Observer minta**: A nézet frissül, ha változik a játék állapota.
- **Strategy minta**: Különböző pontszámítást, kiválasztási logikát lehet alkalmazni (pl. normál vs. „hacker” mód).
- **Command minta**: Műveletek visszavonása / újrajátszás funkcióhoz.

## 📋 Modulok és osztályok

### `GameController` (SRP)
- Feladata: a felhasználói interakciók kezelése
- Kommunikál a `GameService` és `GameView` között

### `GameService` (SRP + OCP)
- Tartalmazza a játékmenet logikáját
- Szavak kezelése, pontszámítás, visszajelzések
- Bővíthető új szabályrendszerekkel (Strategy minta)

### `WordListProvider`
- Felelős a napi szólisták kezeléséért és generálásáért

### `ScoreManager`
- Játékos pontszámának kezelése
- Tárolás fájlban / memóriában

### `Logger`
- Alkalmazás logolása (fájlba írás, hibakezelés)

## 📌 UI terv (JavaFX)

- **Főképernyő**: új játék indítása, statisztikák
- **Játéktábla**: szólista, visszajelzések (x/y)
- **Visszajelzési ablak**: pontszám, újraindítás
- **Admin felület**: statisztika, napi szólista frissítése (opcionális)

## 🪵 Naplózás

- Napló fájlba (`/logs/game.log`)
- Mentett: próbálkozások száma, hibák, nyertes játékok
- Dev log (külön fájlban): ki mit mikor csinált (pl. `dev-log.md`)

## 📘 Fejlesztési ütemterv

### Fázis 1 – Alapverzió
- [x] Projekt inicializálása
- [x] Git repo és Kanban beállítása
- [x] Alap MVC struktúra
- [ ] Szólista generátor és alap UI

### Fázis 2 – Játékmenet és logika
- [ ] Pontszámítás implementálása
- [ ] Helyes karakterek visszajelzése
- [ ] Observer / Strategy minta alkalmazása

### Fázis 3 – UI fejlesztés és logolás
- [ ] UI styling (JavaFX)
- [ ] Logolás implementálása
- [ ] Command minta visszavonási funkcióval

### Fázis 4 – Tesztelés és dokumentáció
- [ ] JUnit tesztek
- [ ] README és végleges rendszerterv kiegészítése

## 🧪 Bővítési lehetőségek

- Hálózatos (LAN) többjátékos mód
- Highscore táblázat
- Extra játékmódok: véletlenszerű karaktercsere, időkorlátos játék

---
