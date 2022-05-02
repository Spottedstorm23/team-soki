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
import java.util.Locale;
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

    String text1 = "Dies ist die erste Zeile von \"SOKI\"";
    String text2 = "Es beginnt mit einer Abzweigung nach rechts, links oder geradeaus.";
    String text3 = "Wohin willst du gehen?";
    String[] alltext = {text1, text2, text3}; // INPUT STORY SECTION HERE

    String textrechts = "Du gehst nach rechts...";
    String textlinks = "Du gehst nach links...";
    String textgeradeaus = "Du gehst gerade aus...";
    // 0         1            2
    String[] richtungsArray = {textrechts, textlinks, textgeradeaus};
    String[] allgMoeglichkeiten = {"menu", "beenden"};

    // if possible: how to add all elements from one array to another?
    String[] moeglichkeitenArray = {"rechts", "links", "geradeaus", allgMoeglichkeiten[0], allgMoeglichkeiten[1]};

    int currentline;

    public void setNextStoryLine() {
        if (currentline < alltext.length) {
            timer.start();
        } else {
            currentline = -1;
            timer.start();
        }
    }

    /*public void initialize() {
        currentline = 0; // START AT alltext[0]
        setNextStoryLine();
        textFieldGameWindow.setOnAction(e -> {
            String inputText = textFieldGameWindow.getText();
            textAreaGameWindow.appendText(">> " + inputText + "\n");
            textFieldGameWindow.clear();

            // TODO: inputText-Interaktionen/-Reaktionen
            // "inoputText"-Interaktionen hier rein!
            try {
                checkInput(inputText);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

    }*/

    public void initialize() {
        String inputText = "Waehle B";
       processUserInput(inputText);
    }


    Timer timer = new Timer(80, new ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            textFieldGameWindow.setDisable(true);
            if (currentline >= 0) {
                // ONE SECTION
                char character[] = alltext[currentline].toCharArray();

                for (int j = 0; j < character.length; j++) {
                    String s = String.valueOf(character[j]);
                    System.out.print(s);
                    textAreaGameWindow.appendText(s);
                    try {
                        Thread.sleep(10L); // USE "50L" FOR A GOOD PACE FOR THE GAME
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } // for (int j = 0; j < character.length; j++)

                textAreaGameWindow.appendText("\n"); // neue Zeile beginnen
                timer.stop();

                if (alltext != richtungsArray) {
                    textFieldGameWindow.setDisable(false);
                    currentline++;
                    setNextStoryLine();
                }

            } else {
                if (alltext != richtungsArray) {
                    alltext = richtungsArray;
                } // if
                timer.stop();

                textFieldGameWindow.setDisable(false);
            } // else
        } // function "actionPerformed"
    });

    public void checkInput(String toBeCheckedInput) throws IOException {
        switch (toBeCheckedInput) {
            case "rechts":
                currentline = 0; // rechts (richtungsArray)
                break;
            case "links":
                currentline = 1; // links (richtungsArray)
                break;
            case "geradeaus":
                currentline = 2; // geradeaus (richtungsArray)
                break;
            case "hilfe":
                currentline = -1;
                break;
            case "beenden":
                currentline = -2;
                break;
            case "menu":
                openMenu();
            default:
                currentline = -3;
                break;
        }

        switch (currentline) {
            case 0:
            case 1:
            case 2:
                alltext = richtungsArray;
                timer.start();
                break;

            case -1:
                textAreaGameWindow.appendText(arrayWithoutBrackets(moeglichkeitenArray));
                break;

            case -2:
                // TODO: SAVE GAME HERE
                quitgame();
                break;

            case -3:
                textAreaGameWindow.appendText("KEIN ÜBEREINSTIMMENDER STRING\nBENUTZE \"hilfe\" FÜR VORSCHLÄGE\n");
                break;
        }
    }


    public void processUserInput(String input) {
        // Input wird in kleine Buchstaben umgewandelt um weiterarbeit zu vereinfachen
        String lowerCaseInput = input.toLowerCase();

        // Es wird ein Matcher erstellt der den Input auf verbotene Zeichen(0-9 sowie Sonderzeichen) überprüft.
        // Grund: Es soll vermieden werden, dass Eingaben gemacht werden, die den Ablauf des Programms stören
        Pattern nonLettersRegex = Pattern.compile("[^a-z ]+");
        Matcher includesNonLetters = nonLettersRegex.matcher(lowerCaseInput);

        if (includesNonLetters.find()) {
            //TODO Ausgabe einer ordentlichen Fehlermeldung für den Nutzer
            System.out.println("Beinhaltet Zahlen oder Sonderzeichen!");
            return;
        }

        if (lowerCaseInput.matches("hilfe")) {
            textAreaGameWindow.appendText("Eine Liste der möglichen Befehle:\n");
            //TODO Liste mit möglichen Befehlen und erklärungen ausgeben
        } else if (lowerCaseInput.matches("menu")) {
            textAreaGameWindow.appendText("Du kehrst ins Hauptmenü zurück!\n");
            //TODO Callback zum Hauptmenü

        } else if (lowerCaseInput.matches("beenden")) {
            // TODO Speichern
            quitgame();
        } else if (lowerCaseInput.matches("untersuche [a-z]+")) {
            String targetObject = lowerCaseInput.replace("untersuche ", "");
            textAreaGameWindow.appendText("Du untersuchst " + targetObject + "!\n");
            //TODO return target und nutze entsprechenden dialog

        } else if (lowerCaseInput.matches("nimm [a-z]+")) {
            String targetItem = lowerCaseInput.replace("nimm ", "");
            textAreaGameWindow.appendText("Du nimmst " + targetItem + "!\n");
            //TODO return target und nutze entsprechenden dialog

        } else if (lowerCaseInput.matches("inventar")) {
            textAreaGameWindow.appendText("Du öffnest dein Ineventar und siehst die deine Items an!\n");
            //TODO Inventarliste ausgeben

        } else if (lowerCaseInput.matches("benutze [a-z ]+")) {
            //TODO Ziel(e) zurückgeben und entsprechnden Dialog ausgeben
            if (lowerCaseInput.contains("mit")) {
                String[] substrings = lowerCaseInput.split("[a-z]* ");
                System.out.println(Arrays.toString(substrings));
                String targetedObject = substrings[3];
                String usedItem = substrings[2];
                textAreaGameWindow.appendText("Du benutzt " + usedItem + "mit " + targetedObject + "\n");

            } else {
                String usedItem = lowerCaseInput.replace("benutze ", "");
                textAreaGameWindow.appendText("Du benutzt " + usedItem + "\n");

            }

        } else if (lowerCaseInput.matches("interagiere mit [a-z]+")) {
            //TODO Ziel(e) zurückgeben und entsprechnden Dialog ausgeben
            String target = lowerCaseInput.replace("interagiere mit ", "");
            textAreaGameWindow.appendText("Du interagierst mit " + target + "\n");

        } else if (lowerCaseInput.matches("gehe zu [a-z ]+")) {
            //TODO Ort zurückgeben und entsprechnden Dialog ausgeben
            String targetPlace = lowerCaseInput.replace("gehe zu ", "");
            textAreaGameWindow.appendText("Du gehst zum " + targetPlace + "\n");

        } else if (lowerCaseInput.matches("waehle [a-z]+")) {
            String option = lowerCaseInput.replace("waehle ", "");
            textAreaGameWindow.appendText("Du wählst Option " + option + "\n");

        } else {
            textAreaGameWindow.appendText("Du hast dich selbst verwirrt und versuchst deine Aktion neuzustarten");
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
        menuStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/icons/Soki-Icon.png")));
        menuStage.show();
    }

    public String arrayWithoutBrackets(String[] stringArray) {
        StringBuilder builder = new StringBuilder();
        for (String value : stringArray) {
            builder.append(value + "\n");
        }
        String text = builder.toString();

        return text;
    }

    public void quitgame() {
        Platform.exit(); // CLOSES ALL STAGES
    }

}
