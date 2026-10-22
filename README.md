# Dammen

Een damspel voor twee spelers op een bord van 10 op 10 velden, gemaakt met Java 21 en
JavaFX voor het vak Project Java aan de Karel de Grote Hogeschool.

## Het spel

- Bord van 100 velden, er wordt gespeeld op de 50 donkere velden.
- Elke speler begint met 20 schijven: wit onderaan, zwart bovenaan. Wit begint.
- Een gewone schijf schuift en slaat schuin naar voren.
- **Slaan is verplicht**, en wie na het slaan opnieuw kan slaan, moet doorslaan.
- Wie de overkant bereikt, krijgt een dam. Een dam mag voor- en achteruit, over
  meerdere velden, en slaat ook op afstand.
- Je wint als je tegenstander geen schijven meer heeft of geen zet meer kan doen.
- Wordt er 50 halve zetten na elkaar enkel met dammen geschoven zonder te slaan,
  dan eindigt het spel in remise.

Een schijf verzetten kan op twee manieren: slepen (drag and drop), of klikken op de
schijf en daarna op het veld waar ze naartoe moet. De velden waar je naartoe mag,
lichten groen op.

## Downloaden en spelen

Onder [Releases](https://github.com/fbugri-rgb/dammen/releases) staat het spel
kant-en-klaar voor elk besturingssysteem. Java zit erin, je hoeft niets te
installeren.

| Computer | Bestand | Starten |
|---|---|---|
| Windows | `Dammen-windows.zip` | Uitpakken en `Dammen.exe` in de map `Dammen` openen. Klik bij "Windows heeft uw pc beschermd" op *Meer info* > *Toch uitvoeren*. |
| macOS (Apple Silicon) | `Dammen-macos.dmg` | Openen en Dammen naar *Programma's* slepen. De eerste keer: *Systeeminstellingen* > *Privacy en beveiliging* > *Toch openen*. |
| Linux | `Dammen-linux.tar.gz` | Uitpakken en `Dammen/bin/Dammen` starten. |

Deze versies bewaren de topscores en instellingen in de map `Dammen` in je thuismap.

Een nieuwe release maken: push een tag, bijvoorbeeld
`git tag v1.0.0 && git push origin v1.0.0`. GitHub Actions bouwt dan de drie versies
(zie `.github/workflows/builds.yml`). Zelf een versie maken voor je eigen computer kan
met `bash scripts/package.sh`, het resultaat komt in `target/dist`.

## Starten vanuit de broncode

Enkel **Java 21** (of hoger) moet geïnstalleerd zijn. Maven en JavaFX worden de eerste
keer vanzelf gedownload, dat kan even duren.

- **Dubbelklikken:** het bestand `spelen.bat` in de projectmap (Windows).
- **In IntelliJ IDEA:** open de projectmap (het project wordt vanzelf ingelezen via
  `pom.xml`), kies bovenaan de startconfiguratie **"Dammen spelen"** en klik op de
  groene pijl. Je kan ook gewoon met de rechtermuisknop op `Launcher.java` klikken en
  *Run* kiezen.
- **In een terminal:** `mvnw javafx:run` (Windows) of `./mvnw javafx:run`
  (macOS/Linux). Maven hoeft daarvoor niet geïnstalleerd te zijn.

### Waarom er een aparte klasse `Launcher` is

`Main` erft over van `Application`, en Java weigert zo'n klasse rechtstreeks te
starten zolang JavaFX niet als **module** geladen is. Wie `Main` start, krijgt daarom:

```
Error: JavaFX runtime components are missing, and are required to run this application
```

`Launcher` erft niet over van `Application` en roept enkel `Main.main` op. Daardoor
gebeurt die controle niet en zijn er geen VM-opties of paden naar JavaFX nodig, op
Windows, macOS en Linux. Start het spel dus altijd via `Launcher`.

## De testen uitvoeren

Er zijn 100 JUnit-testen die de spelregels, het terugnemen van zetten, de
computertegenstander en het werken met bestanden nakijken. Alle resultaten komen op
het scherm.

- **In IntelliJ:** kies bovenaan de startconfiguratie **"Alle testen"** en klik op de
  groene pijl.
- **Dubbelklikken:** het bestand `test.bat` in de projectmap.
- **In een terminal:** `mvn test`

## Schermen

Startscherm, spelscherm, helpscherm met de spelregels, topscorescherm, aboutscherm,
eindscherm, instellingenscherm en een herhalingsscherm.

## Uitbreidingen

- **Tegen de computer spelen.** De computer rekent zes halve zetten vooruit met
  minimax en alpha-bèta snoeien.
- **Zetten terugnemen.** Kies met de teller hoeveel zetten je terug wil.
- **Het spel herbekijken.** Het gespeelde spel wordt opnieuw afgespeeld, waarbij de
  schijven van veld naar veld schuiven.
- **Instellingen.** De kleuren van het bord en de schijven en het geluid aan of uit.
  Die instellingen blijven bewaard voor de volgende keer.

## Opbouw van de code

Het project volgt het Model-View-Presenter patroon:

```
be.kdg.dammen          de startklassen
be.kdg.dammen.model    de spelregels en het werken met bestanden (geen JavaFX)
be.kdg.dammen.view     per scherm een package met een View en een Presenter
```

De volledige spelsituatie zit in een tweedimensionale array van 10 op 10 in de klasse
`Board`. In `plan.md` staat uitgebreider hoe het project opgebouwd is en welke keuzes
er gemaakt zijn.

## Auteur

Farok Bugri — Karel de Grote Hogeschool, Toegepaste Informatica.
