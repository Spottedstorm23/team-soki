package soki.textadventure.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameController {   // Controller for soki-game.fxml
    /*
    IMPORTANT:
    textAreaGameWindow AND textFieldGameWindow CURRENTLY IN .fxml DISABLED
    OPACITY = 1.0 --> SEEN AS ENABLED
    textFieldGameWindow MANUALLY ENABLED, IF TIMER IS NOT RUNNING
     */

    @FXML
    private TextArea textAreaGameWindow;
    @FXML
    private TextField textFieldGameWindow;
    @FXML
    private Label labelUserStandortGameWindow;

    //TODO Diese Liste bitte ordentlich abspeichern
    private String listOfPossibleInputs = "Hier eine Liste mit Befehelen die dir zur Verfügung stehen\n" +
            "\uF076 <Menu>: Das Spiel speichert und du kehrst ins Hauptmenü zurück\n" +
            "\uF076 <Beenden>: Beendigung des Spiels nach dem Speichern.\n" +
            "\uF076 <Untersuche {Ziel}>: Ein Item, Charakter oder Umgebungsobjekt wird untersucht.\n" +
            "\uF076 <Nimm {Item}>: Das gewählte Item wird im Inventar verstaut.\n" +
            "\uF076 <Inventar>: Eine Liste der sich aktuellen Items im Inventar wird ausgegeben.\n" +
            "\uF076 <Benutze {Item} (mit {Ziel})>: Das gewählte Item wird\n" +
            "eingesetzt. Das Ziel kann optional angebenen werden.\n" +
            "\uF076 <Interagiere mit {Ziel}>:Interaktion mit einem Umgebungsgegenstand oder einem Charakter.\n" +
            "\uF076 <Gehe zu {Ort}>: Bewegung zum ausgewählten Ort\n" +
            "\uF076 <Waehle {Option})>: In speziellen Situationen können weiter Eingaben abgefragt werden.\n" +
            " Diese werden in der Regel als Liste zur Verfügung gestellt";

    private String currentDialog;

    //Eventuell für die Abfrage der Dialoge benötigten Werte?
    // o - Prolog; 1 - Chapter 1; 2 - Chapter 2; 3 - Chapter 3; 4 - Chapter 4;
    private int chapter = 0;
    // Start immer mit dem ersten Block?
    private int dialogBlock = 1;

    public void setCurrentDialogLine(String text) {
        this.currentDialog = text;
        timer.start();

    }

    public void initialize() {
        setCurrentDialogLine("Hallo, was möchtest du machen?");
        textFieldGameWindow.setOnAction(e -> {
            String inputText = textFieldGameWindow.getText();
            textAreaGameWindow.appendText(">> " + inputText + "\n");
            textFieldGameWindow.clear();
            try {
                processUserInput(inputText);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }


    Timer timer = new Timer(80, new ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            char[] character = currentDialog.toCharArray();

            for (char c : character) {
                String s = String.valueOf(c);
                textAreaGameWindow.appendText(s);
                try {
                    Thread.sleep(10L); // USE "50L" FOR A GOOD PACE FOR THE GAME
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            textAreaGameWindow.appendText("\n"); // neue Zeile beginnen
            timer.stop();
            textFieldGameWindow.setDisable(false);
        }
    });


    public void processUserInput(String input) throws IOException {
        String lowerCaseInput = input.toLowerCase();

        // Es wird ein Matcher erstellt der den Input auf verbotene Zeichen(0-9 sowie Sonderzeichen) überprüft.
        // Grund: Es soll vermieden werden, dass Eingaben gemacht werden, die den Ablauf des Programms stören
        Pattern nonLettersRegex = Pattern.compile("[^a-z ]+");
        Matcher includesNonLetters = nonLettersRegex.matcher(lowerCaseInput);

        if (includesNonLetters.find()) {
            //TODO Ausgabe einer ordentlichen Fehlermeldung für den Nutzer
            textAreaGameWindow.appendText("Du hast dich selbst verwirrt und versuchst deine Aktion neuzustarten\n");
            return;
        }

        if (lowerCaseInput.matches("hilfe")) {
            setCurrentDialogLine(listOfPossibleInputs);

        } else if (lowerCaseInput.matches("menu")) {
            setCurrentDialogLine("Du kehrst ins Hauptmenü zurück!\n");
            // TODO Speichern
            openMenu();

        } else if (lowerCaseInput.matches("beenden")) {
            // TODO Speichern
            quitgame();

        } else if (lowerCaseInput.matches("untersuche [a-z]+")) {
            String targetObject = lowerCaseInput.replace("untersuche ", "");
            setCurrentDialogLine("Du untersuchst " + targetObject + "!\n");
            //TODO return target und nutze entsprechenden dialog

        } else if (lowerCaseInput.matches("nimm [a-z]+")) {
            String targetItem = lowerCaseInput.replace("nimm ", "");
            setCurrentDialogLine("Du nimmst " + targetItem + "!\n");
            //TODO return target und nutze entsprechenden dialog

        } else if (lowerCaseInput.matches("inventar")) {
            setCurrentDialogLine("Du öffnest dein Ineventar und siehst die deine Items an!\n");
            //TODO Inventarliste ausgeben

        } else if (lowerCaseInput.matches("benutze [a-z ]+")) {
            //TODO Ziel(e) zurückgeben und entsprechnden Dialog ausgeben
            if (lowerCaseInput.matches("benutze [a-z]+ mit[ ]*")) {
                setCurrentDialogLine("Bitte gib ein Ziel für deine Aktion ein!\n");
            } else if (lowerCaseInput.contains("mit")) {
                String[] substrings = lowerCaseInput.split(" ");
                System.out.println(Arrays.toString(substrings));
                String targetedObject = substrings[3];
                String usedItem = substrings[1];
                setCurrentDialogLine("Du benutzt " + usedItem + " mit " + targetedObject + "\n");

            } else {
                String usedItem = lowerCaseInput.replace("benutze ", "");
                setCurrentDialogLine("Du benutzt " + usedItem + "\n");
            }

        } else if (lowerCaseInput.matches("interagiere mit [a-z]+")) {
            //TODO Ziel(e) zurückgeben und entsprechnden Dialog ausgeben
            String target = lowerCaseInput.replace("interagiere mit ", "");
            setCurrentDialogLine("Du interagierst mit " + target + "\n");

        } else if (lowerCaseInput.matches("gehe zu [a-z ]+")) {
            //TODO Ort zurückgeben und entsprechnden Dialog ausgeben
            String targetPlace = lowerCaseInput.replace("gehe zu ", "");
            setCurrentDialogLine("Du gehst zum " + targetPlace + "\n");

        } else if (lowerCaseInput.matches("waehle [a-z]+")) {
            String option = lowerCaseInput.replace("waehle ", "");
            setCurrentDialogLine("Du wählst Option " + option + "\n");

        } else {
            setCurrentDialogLine("Du hast dich selbst verwirrt und versuchst deine Aktion neuzustarten. \n Tipp: Nutze Hilfe wenn du nicht weiter weißt");
        }

    }

    public void openMenu() throws IOException {
        Stage stage = (Stage) textFieldGameWindow.getScene().getWindow();
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/soki-menu.fxml"));
        Parent rootGame = fxmlLoader.load();
        Scene menuScene = new Scene(rootGame);
        Stage menuStage = new Stage();
        menuStage.setTitle("SOKI");
        // menuStage.initStyle(StageStyle.TRANSPARENT); // remove close, minimize, full screen option
        // menuStage.setFullScreen(true); // full screen
        menuStage.setResizable(false); // cannot change window size
        menuStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // cannot exit full screen
        menuStage.setScene(menuScene);
        menuStage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/icons/Soki-Icon.png"))));
        menuStage.show();
    }

    public void quitgame() {
        System.exit(0); // CLOSES ALL STAGES
    }

}
