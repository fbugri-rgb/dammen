# Projectplan — Dammen (Java Programming I, KdG)

## 1. Gekozen aanpak (beslissingen)

| Onderwerp | Keuze |
|---|---|
| Taal UI | Nederlands (alle knoppen, labels, meldingen) |
| Taal code | Engels (klassen, methodes, variabelen) — zoals de checklist aanraadt |
| Verzetten schijf | Klik-klik **en** drag/drop (beide) |
| Spelregels | Vereenvoudigd: schijven bewegen én slaan enkel voorwaarts, dammen mogen ver/achteruit |
| Team | Solo — Farok Bugri |
| Java / JavaFX | Java 21, JavaFX 21.0.6 (Maven) |
| Package root | `be.kdg.dammen` |

### Spelregels zoals geïmplementeerd
1. Bord van 10x10, gespeeld op de **donkere** velden. 20 witte + 20 zwarte schijven.
2. Wit begint. Spelers wisselen om beurt.
3. Een gewone schijf (`MAN`) schuift 1 veld diagonaal **voorwaarts** naar een leeg veld.
4. Slaan: een schijf springt diagonaal **voorwaarts** over een aangrenzende tegenstander
   naar het lege veld erachter. De geslagen schijf gaat van het bord.
5. **Slaan is verplicht.** Kan je slaan, dan mag je geen gewone zet doen.
6. Kan je na het slaan opnieuw slaan met dezelfde schijf, dan **moet** je doorslaan
   (meerslag). De beurt gaat pas over als er niet meer geslagen kan worden.
7. Bereikt een schijf de overkant (damlijn), dan wordt het een **dam** (`KING`).
   Word je dam midden in een slagreeks, dan stopt de beurt daar (klassieke regel).
8. Een dam schuift over **meerdere** velden diagonaal, voor- en achteruit, en slaat
   op afstand: precies één tegenstander op de lijn, daarna een leeg veld.
9. Winst als de tegenstander geen schijven meer heeft, of geen enkele zet meer kan doen.

> Bewust **niet** geïmplementeerd: de meerslagregel/majoriteitsregel ("je moet altijd de
> slagzet spelen die het meeste schijven slaat"). Bewuste keuze om de code eenvoudig
> en leesbaar te houden; is vermeld in de about-/helpteksten.

## 2. Architectuur (MVP, volgens de cheat sheet)

```
src/main/java/be/kdg/dammen/
├── DammenMain.java                 ← Application, start het startscherm
├── DammenLauncher.java             ← startklasse (zie opmerking hieronder)
├── model/                          ← GEEN JavaFX imports
│   ├── DraughtsGame.java           ← hoofdmodel: beurt, status, zetgeschiedenis
│   ├── Board.java                  ← Piece[10][10]  (2D-array vereiste)
│   ├── Piece.java                  ← kleur + type
│   ├── PieceColor.java  (enum)     ← WHITE, BLACK
│   ├── PieceType.java   (enum)     ← MAN, KING
│   ├── Position.java               ← rij + kolom, met validatie
│   ├── Move.java                   ← van/naar + eventueel geslagen schijf
│   ├── MoveGenerator.java          ← welke zetten zijn geldig? verplicht slaan?
│   ├── GameStatus.java  (enum)     ← PLAYING, WHITE_WON, BLACK_WON
│   ├── Player.java                 ← naam + kleur
│   ├── HighScore.java              ← naam, aantal zetten, datum
│   ├── HighScoreRepository.java    ← lezen/schrijven highscores.txt
│   ├── GameStorage.java            ← spel opslaan/laden (extern bestand)
│   ├── RulesReader.java            ← leest spelregels.txt in
│   ├── BoardSnapshot.java          ← foto van het bord, voor het terugnemen
│   ├── ComputerPlayer.java         ← de computertegenstander (minimax)
│   ├── Settings.java               ← kleuren en geluid
│   ├── SettingsRepository.java     ← instellingen.properties lezen/schrijven
│   └── DraughtsException.java      ← eigen RuntimeException
└── view/                           ← per scherm een package
    ├── DialogHelper.java           ← alle Alerts op één plaats
    ├── ScreenHelper.java           ← opent help/topscores/about in nieuwe Stage
    ├── SoundPlayer.java            ← speelt de geluidjes af
    ├── startscherm/                StartschermView + StartschermPresenter
    ├── spelscherm/                 SpelschermView + SpelschermPresenter
    │                               + BoardPane (tekent het bord)
    │                               + GameFileChooser (bestandsvenster)
    ├── helpscherm/                 HelpschermView + HelpschermPresenter
    ├── highscorescherm/            HighscoreschermView + ...Presenter
    ├── aboutscherm/                AboutschermView + AboutschermPresenter
    ├── instellingenscherm/         InstellingenschermView + ...Presenter
    ├── herhalingscherm/            HerhalingschermView + ...Presenter
    └── eindescherm/                EindeschermView + EindeschermPresenter
src/main/resources/be/kdg/dammen/view/
├── css/style.css
└── data/spelregels.txt
```

**Waarom een aparte DammenLauncher-klasse?**
Java weigert een klasse te starten die overerft van `Application` zolang JavaFX niet
als *module* geladen is. Omdat JavaFX hier via Maven op het klassenpad staat, start
`DammenLauncher` (die niet overerft van `Application`) het spel op. Zo werkt de groene
pijl in IntelliJ altijd. `DammenMain` blijft de gewone Application-klasse uit de les.

**MVP-afspraken die we strikt volgen**
- Model kent de view niet en importeert nooit JavaFX.
- View bevat enkel UI-opbouw (`initialiseNodes()` + `layoutNodes()`), getters zijn
  *package-private*, geen setters waar het vermeden kan worden.
- Presenter is de enige die model én view kent: `addEventHandlers()` + `updateView()`.
- Exceptions: **throwen in het model**, **catchen in de presenter** → nette `Alert`.

## 3. Aftekenlijst evaluatiecriteria

### Werking
- [x] 2 menselijke spelers spelen om beurt, spelregels afgedwongen
- [x] Melding bij ongeldige zet (Alert + boodschap onderaan)
- [x] Melding wanneer er geslagen moet worden ("Opgelet: slaan is verplicht!")
- [x] Melding wanneer iemand dam haalt (Alert "Een dam erbij!")
- [x] Melding wanneer het spel gedaan is (eindscherm met de uitslag)
- [x] Geslagen schijven komen in een apart vak naast het bord
- [x] Spelsituatie opslaan naar extern bestand + terug inladen (`*.dam`)
- [x] Minstens 5 schermen → start, spel, help, highscore, about, einde,
      instellingen, herhaling (8)
- [x] Hoofdscherm + helpscherm + highscorescherm + aboutscherm aanwezig
- [x] Applicatie crasht nooit; foute input onmogelijk; zinvolle feedback
- [x] Herschaalbaar, uitgelijnd, marges, één taal, geen schrijffouten
- [x] Menubalk bovenaan (op het startscherm én het spelscherm)

### Technische realisatie
- [x] ≥ 5 controls: Button, Label, TextField, MenuBar, MenuItem, ListView,
      TextArea, ScrollPane, Separator, RadioButton, CheckBox, ColorPicker,
      Spinner, Slider
- [x] ≥ 3 events: `setOnAction`, `setOnMouseClicked`, `setOnDragDetected` /
      `setOnDragOver` / `setOnDragDropped`, `setOnCloseRequest`
- [x] ≥ 3 layoutpanes: BorderPane, GridPane, VBox, HBox, FlowPane, StackPane
- [x] ≥ 1 keer van root veranderen (`view.getScene().setRoot(...)`: start → spel)
- [x] ≥ 1 keer van Scene veranderen (`stage.setScene(...)`: spel → eindscherm)
- [x] ≥ 1 keer van Stage veranderen (help, topscores en about in een nieuwe Stage)
- [x] MVP correct toegepast, packagestructuur uit de les
- [x] Bestand inlezen (spelregels) + wegschrijven (highscores + savegame)
- [x] IOExceptions correct afgehandeld (wrappen in `DraughtsException`)
- [x] Code voldoet aan de kwaliteitschecklist (klasse ≤ 1 verantwoordelijkheid,
      methodes ≤ 20 lijnen, attributen private, Javadoc bovenaan elke klasse)

## 5. Wat is er getest?

### Automatische testen (JUnit 5)
In `src/test/java/be/kdg/dammen/model/` staan **98 testen**. Ze duren samen ongeveer
5 seconden en zijn allemaal groen.

**Alle testen in één keer uitvoeren — kies wat je het handigst vindt:**

1. **In IntelliJ (aangeraden):** kies bovenaan in de lijst met startconfiguraties
   **"Alle testen"** en klik op de groene pijl. Die configuratie zit mee in het
   project (`.run/Alle testen.run.xml`), je hoeft dus niets in te stellen.
2. **Dubbelklikken:** het bestand **`test.bat`** in de projectmap voert alle testen
   uit en eindigt met een duidelijke boodschap "ALLE TESTEN ZIJN GESLAAGD".
3. **In een terminal:** `mvn test`

Alle resultaten verschijnen **op het scherm**, ook wanneer een test faalt: je ziet
dan meteen welke test, welke boodschap en op welke regel het misliep. Daarvoor staat
bij de surefire-plugin in `pom.xml` de instelling `<useFile>false</useFile>`; anders
schrijft Maven die informatie naar tekstbestanden in `target/surefire-reports` en
moet je die eerst gaan openen.

| Testklasse | Aantal | Wat wordt er nagekeken |
|---|---|---|
| `PositionTest` | 6 | veldnummers 1 t.e.m. 50, opschuiven, buiten het bord |
| `BoardTest` | 12 | beginstelling, donkere velden, leegmaken, kopiëren |
| `PieceTest` | 6 | dam worden, kleuren, speelrichting |
| `MoveTest` | 5 | notatie "Wit 32-27" en "Wit 27x18", slaan, dam |
| `DraughtsGameRulesTest` | 24 | **de spelregels**: beurten, achteruit weigeren, slagplicht, doorslaan, dam halen, dam op afstand, winnen, remise |
| `DraughtsGameUndoTest` | 9 | zetten terugnemen, geslagen schijven terugzetten, dam terugdraaien |
| `ComputerPlayerTest` | 10 | speelt geldige zetten, kiest de veilige slagzet, loopt niet in de val |
| `GameStorageTest` | 9 | opslaan en inlezen, fout of onvolledig bestand |
| `HighScoreRepositoryTest` | 9 | bewaren, sorteren, hoogstens tien, beschadigde lijnen |
| `SettingsRepositoryTest` | 7 | standaardwaarden, bewaren en terug inlezen, foute kleuren |
| `RulesReaderTest` | 1 | de spelregels worden uit het tekstbestand gelezen |

De testen die met bestanden werken, gebruiken een **tijdelijke map** (`@TempDir`).
De echte `highscores.txt` en `instellingen.properties` worden dus nooit overschreven.
Daarvoor hebben `HighScoreRepository` en `SettingsRepository` een tweede constructor
gekregen die een bestand meekrijgt.

### Met de hand uitgetest
De applicatie werd opgestart en doorlopen: startscherm, een spel tegen de computer
(hij antwoordt zelf), een zet terugnemen, het helpscherm dat de spelregels uit het
tekstbestand haalt, het instellingenscherm (kleuren aanpassen en bewaren) en het
herhalingsscherm met de schuivende schijven.

## 6. Uitbreidingen (alle vier uitgewerkt)

### a. Spelen tegen de computer
- `ComputerPlayer` speelt de zwarte schijven. Kies op het startscherm
  "Tegen de computer".
- Werkwijze: **minimax met alpha-bèta snoeien**, 6 halve zetten diep. Voor elke zet
  maakt de computer een kopie van het spel (`copyForThinking()`), speelt de zet daarop
  en kijkt wat de tegenstander dan zou doen, enzovoort. Elke stelling krijgt een cijfer
  (materiaal, dammen tellen zwaarder, hoe ver een schijf opgeschoven is, schijven aan
  de rand en op de eigen achterlijn).
- Gemeten: hoogstens ~1,4 seconden per zet in het moeilijkste eindspel, meestal
  minder dan 100 ms. De computer speelt een volledig spel uit zonder fouten, slaat de
  veilige schijf en loopt niet in een tegenslag.

### b. Zetten terugnemen (undo)
- Voor elke zet wordt een foto (`BoardSnapshot`) van de spelsituatie bewaard.
- Met de spinner kies je **hoeveel** zetten je terugneemt, de knop of het menu doet de rest.
- Speel je tegen de computer, dan gaat het terugnemen door tot jij weer aan zet bent.
- Van een spel dat uit een bestand geladen werd, kan je enkel de zetten van deze
  speelbeurt terugnemen (van de oudere zetten bestaan er geen foto's meer).

### c. Het spel herbekijken met animatie
- Apart scherm (`herhalingscherm`) dat het spel vanaf de beginstelling opnieuw
  naspeelt. Bij elke zet **schuift** de schijf van haar oude naar haar nieuwe veld
  (`TranslateTransition`).
- Knoppen: afspelen/pauze, vorige zet, volgende zet, opnieuw vanaf het begin, en een
  schuifbalk voor de snelheid. De laatste zet wordt op het bord aangeduid.
- Te openen via het eindscherm ("Spel herbekijken") of tijdens het spel via
  Spel > Zetten herbekijken.

### d. Instellingenscherm dat bewaard blijft
- Apart venster met vier kleurkiezers (lichte velden, donkere velden, witte schijven,
  zwarte schijven) en een vinkje voor het geluid.
- Wordt bewaard in `instellingen.properties` en **ingelezen bij de volgende run**.
- Geluid: vier eigen geluidsbestandjes (verzetten, slaan, dam, winnen) die via
  `SoundPlayer` afgespeeld worden.

### Extra regel die hierbij nodig was: remise
Bij het testen bleek dat twee dammen eindeloos heen en weer kunnen schuiven, waardoor
een spel nooit eindigde. Daarom: worden er 50 halve zetten na elkaar enkel met dammen
gespeeld zonder te slaan, dan eindigt het spel in **remise**.

## 4. Volgorde van uitvoeren
1. Maven/JavaFX skeleton opzetten (archetype-rommel opruimen)
2. Model: Position, Piece, Board, Move → MoveGenerator → DraughtsGame
3. Bestandsklassen: RulesReader, HighScoreRepository, GameStorage
4. Views + presenters, scherm per scherm, beginnend bij spelscherm
5. CSS, teksten, afwerking
6. Compileren + effectief laten draaien en uittesten
