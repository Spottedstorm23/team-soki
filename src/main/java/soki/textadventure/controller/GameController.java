package soki.textadventure.controller;

import javafx.concurrent.Task;
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

import javax.swing.*;
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
    /*
    NOTICE:
    Button disabled to let the programm set the focus on the textfield first
    --> TODO: Hide/Disable mouse Cursor while Programm is focused
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
    private FileController fileController = new FileController();

    //Gamevariables
    // o - Prolog; 1 - Chapter 1; 2 - Chapter 2; 3 - Chapter 3; 4 - Chapter 4;
    private int currentChapter;
    private int currentDialogBlock;
    private String playerName;
    private int currentPlayThrough;
    private boolean nameIsSet;
    private String currentLocation;
    private boolean isEnd = false;


    public void setCurrentDialogLine(String text) {
        this.currentDialog = text;
        updateUserLocationInWindow();
        timer.start();
    }

    private boolean isNewGamePlus() {
        return currentPlayThrough > 0 && currentChapter == 0 && currentDialogBlock == 0;
    }

    public void setGameVariables() {
        setCurrentChapterAndDialogBlock(fileController.getPlayerChapter(), fileController.getPlayerDialog());
        this.playerName = fileController.getPlayerName();
        this.currentPlayThrough = fileController.getPlayerPlaythrough();
        this.nameIsSet = !Objects.equals(fileController.getPlayerName(), "user");
        this.currentLocation = fileController.getPlayerLocation();
        this.isEnd = fileController.getIsEnd();
    }

    public void initialize() {
        setGameVariables();
        startFirstDialogueLine();
        updateUserLocationInWindow();
        textFieldGameWindow.setOnAction(e -> {
            String inputText = textFieldGameWindow.getText();
            textAreaGameWindow.appendText(">> " + inputText + "\n");
            textFieldGameWindow.clear();
            try {
                if (nameIsSet) {
                    processUserInput(inputText);
                } else {
                    setUserName(inputText);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void startFirstDialogueLine() {
        setCurrentDialogLine(replacePlaceholderWithName(fileController.getText(currentChapter, currentDialogBlock)));
        //setCurrentDialogLine("0.0");
        if (isNewGamePlus()) {
            setSecondDialogOfNewGame("isFirstPlaythrough", "false");
        } else if (currentChapter == 0 && currentDialogBlock == 0) {
            setSecondDialogOfNewGame("isFirstPlaythrough", "true");
        }
        updateUserLocationInWindow();
    }

    Timer timer = new Timer(80, new ActionListener() {

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            textFieldGameWindow.setDisable(true);   // --> User soll nicht in der Lage sein,
            // während der Timer noch am Schreiben der Story ist,
            // zwischendurch eine Eingabe zu tätigen
            textAreaGameWindow.setDisable(true);
            char[] character = currentDialog.toCharArray();
            for (char c : character) {
                String s = String.valueOf(c);
                textAreaGameWindow.appendText(s);
                try {
                    Thread.sleep(50L); // USE "50L" FOR A GOOD PACE FOR THE GAME
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            textAreaGameWindow.appendText("\n"); // neue Zeile beginnen
            timer.stop();

            textFieldGameWindow.setDisable(false);
            textAreaGameWindow.setDisable(false);
        }
    });

    // Kurze Wartezeit beim Beenden des Spiels (siehe: "if (lowerCaseInput.matches("beenden"))")
    public static void delay(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> continuation.run());
        new Thread(sleeper).start();
    }


    public void processUserInput(String input) throws IOException, InterruptedException {
        String lowerCaseInput = input.toLowerCase();

        // Es wird ein Matcher erstellt der den Input auf verbotene Zeichen(0-9 sowie Sonderzeichen) überprüft.
        // Grund: Es soll vermieden werden, dass Eingaben gemacht werden, die den Ablauf des Programms stören
        Pattern nonLettersRegex = Pattern.compile("[^a-z ]+");
        Matcher includesNonLetters = nonLettersRegex.matcher(lowerCaseInput);

        if (includesNonLetters.find()) {
            textAreaGameWindow.appendText("Du hast dich selbst verwirrt und versuchst deine Aktion neuzustarten\n");
            return;
        }

        if (lowerCaseInput.matches("hilfe")) {
            setCurrentDialogLine(listOfPossibleInputs);

        } else if (lowerCaseInput.matches("menu")) {
            saveGame();
            setCurrentDialogLine("Du kehrst ins Hauptmenü zurück!\n");
            delay(3000, () -> {
                try {
                    openMenu();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } else if (lowerCaseInput.matches("beenden")) {
            saveGame();
            setCurrentDialogLine("Dein Spiel wurde gespeichert. \"SOKI\" wird nun beendet...");
            delay(3000, this::quitgame);
            // oder: delay(3000, () -> quitgame());


        } else if (lowerCaseInput.matches("untersuche [a-z]+")) {
            String targetObject = lowerCaseInput.replace("untersuche ", "");
            findMyDialog("untersuche", targetObject);

        } else if (lowerCaseInput.matches("nimm [a-z]+")) {
            String targetItem = lowerCaseInput.replace("nimm ", "");
            findMyDialog("nimm", targetItem);

        } else if (lowerCaseInput.matches("inventar")) {
            String inventory = Arrays.toString(fileController.listVisibleObjects());
            inventory = inventory.replace("[", "");
            inventory = inventory.replace("]", "");
            if (inventory.equals("[]")) {
                inventory = "Du hast zur Zeit nichts im Inventar";
            }
            inventory = inventory.replace("muenzen", "muenzen (Du hast zur Zeit " + fileController.getCoins() + ")");
            setCurrentDialogLine(inventory);

        } else if (lowerCaseInput.matches("benutze [a-z ]+")) {
            if (lowerCaseInput.matches("benutze [a-z]+ mit[ ]*")) {
                setCurrentDialogLine("Bitte gib ein Ziel für deine Aktion ein!\n");
            }
            /*else if (lowerCaseInput.contains("mit")) {
                String[] substrings = lowerCaseInput.split(" ");
                System.out.println(Arrays.toString(substrings));
                String targetedObject = substrings[3];
                String usedItem = substrings[1];
                setCurrentDialogLine("Du benutzt " + usedItem + " mit " + targetedObject + "\n");

            }*/
            else {
                String usedItem = lowerCaseInput.replace("benutze ", "");
                System.out.println(usedItem);
                findMyDialog("benutze", usedItem);
            }

        } else if (lowerCaseInput.matches("interagiere mit [a-z]+")) {
            String target = lowerCaseInput.replace("interagiere mit ", "");
            findMyDialog("interagiere", target);

        } else if (lowerCaseInput.matches("gehe zu [a-z ]+")) {
            String targetPlace = lowerCaseInput.replace("gehe zu ", "");
            findMyDialog("gehe", targetPlace);

        } else if (lowerCaseInput.matches("waehle [a-z]+")) {
            String option = lowerCaseInput.replace("waehle ", "");
            System.out.println(option);
            findMyDialog("waehle", option);

        } else if (fileController.getIsEnd() && lowerCaseInput.matches("neustart")) {
            textAreaGameWindow.clear();
            //todo maybe change this sleep if needed
            Thread.sleep(1000);
            fileController.setIsEnd(false);
            fileController.changeLocation(0, 0);
            this.currentChapter = 0;
            this.currentDialogBlock = 0;
            startFirstDialogueLine();
        } else {
            setCurrentDialogLine("Du hast dich selbst verwirrt und versuchst deine Aktion neuzustarten. \n Tipp: Nutze Hilfe wenn du nicht weiter weißt");
        }

    }

    private void saveGame() {
        fileController.changeLocation(currentChapter, currentDialogBlock);
        fileController.setPlayerLocation(currentLocation);
        fileController.setPlayerName(playerName);
        fileController.setPlaythrough(currentPlayThrough);
    }

    private void findMyDialog(String command, String target) {
        int[] newChapAndDialog = fileController.getNextDialogNumbersAndExecuteFunction(currentChapter, currentDialogBlock, command, target);
        if (newChapAndDialog == null) {
            newChapAndDialog = fileController.getNextDialogNumbersAndExecuteFunction(currentChapter, currentDialogBlock, "default", "default");
        }
        this.currentChapter = newChapAndDialog[0];
        this.currentDialogBlock = newChapAndDialog[1];
        setCurrentDialogLine(replacePlaceholderWithName(fileController.getText(currentChapter, currentDialogBlock)));
    }

    private void setSecondDialogOfNewGame(String command, String target) {
        int[] newChapAndDialog = fileController.getNextDialogNumbersAndExecuteFunction(currentChapter, currentDialogBlock, command, target);
        this.currentChapter = newChapAndDialog[0];
        this.currentDialogBlock = newChapAndDialog[1];
        currentDialog = currentDialog + "\n" + replacePlaceholderWithName(fileController.getText(currentChapter, currentDialogBlock));
    }

    private void setUserName(String input) {
        String lowerCaseInput = input.toLowerCase();
        Pattern nonLettersRegex = Pattern.compile("[^a-z ]+");
        Matcher includesNonLetters = nonLettersRegex.matcher(lowerCaseInput);

        if (includesNonLetters.find()) {
            setCurrentChapterAndDialogBlock(0, 3);
            setCurrentDialogLine(replacePlaceholderWithName(fileController.getText(currentChapter, currentDialogBlock)));
            fileController.changeLocation(currentChapter, currentDialogBlock);
        } else if (input.equals("user")) {
            setCurrentChapterAndDialogBlock(0, 4);
            setCurrentDialogLine(replacePlaceholderWithName(fileController.getText(currentChapter, currentDialogBlock)));
            fileController.changeLocation(currentChapter, currentDialogBlock);
        } else {
            fileController.setPlayerName(input);
            this.playerName = input;
            this.nameIsSet = true;
            setCurrentChapterAndDialogBlock(0, 5);
            setCurrentDialogLine(replacePlaceholderWithName(fileController.getText(currentChapter, currentDialogBlock)));
            fileController.changeLocation(currentChapter, currentDialogBlock);
        }
    }

    private void setCurrentChapterAndDialogBlock(int chap, int dial) {
        this.currentChapter = chap;
        this.currentDialogBlock = dial;
    }

    private String replacePlaceholderWithName(String text) {
        String newText = text.replace("{Name}", playerName);
        return newText;
    }

    private void updateUserLocationInWindow() {
        this.currentLocation = fileController.getPlayerLocation();
        labelUserStandortGameWindow.setText(currentLocation);
    }

    public void openMenu() throws IOException {
        Stage stage = (Stage) textFieldGameWindow.getScene().getWindow();
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/soki-menu.fxml"));
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
