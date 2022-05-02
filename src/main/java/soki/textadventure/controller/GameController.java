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
        String text = "help 123";
        processUserInput(text);
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

        // Es wird ein Matcher erstellt der den input auf alle Zeichen die nicht kleinbuchstaben oder Leerzeichen sind überprüft.
        // Grund: Es soll vermieden werden, dass Eingaben gemacht werden, die den Ablauf des Programms stören
        Pattern nonLettersRegex = Pattern.compile("[^a-z ]+");
        Matcher includesNonLetters =nonLettersRegex.matcher(lowerCaseInput);

        if (includesNonLetters.find()) {
            System.out.println("Beinhaltet Zahlen oh no !");
            return;
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
