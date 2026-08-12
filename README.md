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

## Starten

Open het project in IntelliJ IDEA (het wordt vanzelf ingelezen via `pom.xml`), kies
bovenaan de startconfiguratie **"Dammen spelen"** en klik op de groene pijl. Die
configuratie start `be.kdg.dammen.Main` op en zit mee in het project.

Vanuit een terminal kan het ook:

```
mvn clean javafx:run
```

### Waarom er VM-opties bij de startconfiguratie staan

`Main` erft over van `Application`, en Java weigert zo'n klasse te starten zolang
JavaFX niet als **module** geladen is. JavaFX komt hier via Maven binnen en staat dus
op het klassenpad, niet op het modulepad. Zonder extra opties krijg je daarom:

```
Error: JavaFX runtime components are missing, and are required to run this application
```

De startconfiguratie lost dat op met deze VM-opties, waarbij `$MAVEN_REPOSITORY$` door
IntelliJ zelf ingevuld wordt met de map `.m2` van de gebruiker:

```
--module-path "$MAVEN_REPOSITORY$/org/openjfx/javafx-base/21.0.6/javafx-base-21.0.6-win.jar;...(graphics, controls, media)..." --add-modules javafx.controls,javafx.media
```

Werk je op macOS of Linux, vervang dan `-win` door `-mac` of `-linux`.
Met `mvn clean javafx:run` is dit nooit nodig: die plugin regelt het modulepad zelf.

## De testen uitvoeren

Er zijn 98 JUnit-testen die de spelregels, het terugnemen van zetten, de
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
