#Textadventure

## Git-/Code - Regeln
* **Code**: englisch (Funktionen, Variablen etc.)
* **Kommentare/ Dokumentation**: deutsch
* **GIT**:
  * erst _pull_, dann _push_
  * _Feature-Branches_ 
    * Name wie das zugehörige Issue 
    * erstellen:
      * `git checkout -b branchname` -> Lokales Erstellen
      * `git push --set-upstream origin branchname` -> Branch in Repo pushen
    * Review durch andere Mitglieder
    * Merge wenn Arbeit am Branch abgeschlossen
  * _Commitmessages_: deutsch/englisch (?)
    * `TASK:` -> abgeschlossene Aufgabe für ein Feature, eventuell mit Beschreibung
    * `WIP:` -> Zwischencommits während der arbeit an einem Feature
    * `FIX:` -> Fehlerbehebung mit eventueller Beschreibung
    * `CLEAN_UP:` -> Aufräumcommits für Mergerequests

## Executable Version erzeugen und starten:
* `mvn install` ausführen (Terminal oder IntelliJ: Execute Maeven Goal)
* **.jar** die in `target` entsteht in `Textadventure_executable/bin` verschieben
* **.jar** in `textadventure.jar` umbenennen
* über launcher.bat starten


## Requirements für Start in Entwicklungsumgebung:
* Java 17.0.2
* JavaFX 17

## Wunschliste für unser Projekt:
* Hauptmenü
* Storyplot
* Styling/ Desing
* Befehleingabe durch Nutzer
* Achivements/ Collectibles
* Speicherfunktion
  * automatisch
  * manuell
  * Achievements
  * Referenzen auf andere Stränge
  * mehrere Speicherslots



