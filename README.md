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

Open het project in IntelliJ IDEA (het wordt vanzelf ingelezen via `pom.xml`) en voer
**`be.kdg.dammen.Main`** uit, of kies bovenaan de startconfiguratie **"Dammen spelen"**.

Vanuit een terminal kan het ook:

```
mvn clean javafx:run
```

> **Krijg je de melding "JavaFX runtime components are missing"?**
> Dan moet IntelliJ nog weten waar JavaFX staat. Ga naar
> *Run → Edit Configurations*, en zet bij **VM options** deze regel
> (pas het pad naar je gebruikersmap aan als dat nodig is):
>
> ```
> --module-path "C:\Users\<gebruiker>\.m2\repository\org\openjfx\javafx-base\21.0.6\javafx-base-21.0.6-win.jar;C:\Users\<gebruiker>\.m2\repository\org\openjfx\javafx-graphics\21.0.6\javafx-graphics-21.0.6-win.jar;C:\Users\<gebruiker>\.m2\repository\org\openjfx\javafx-controls\21.0.6\javafx-controls-21.0.6-win.jar;C:\Users\<gebruiker>\.m2\repository\org\openjfx\javafx-media\21.0.6\javafx-media-21.0.6-win.jar" --add-modules javafx.controls,javafx.media
> ```
>
> Met `mvn clean javafx:run` is dat nooit nodig: die regelt het zelf.

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
