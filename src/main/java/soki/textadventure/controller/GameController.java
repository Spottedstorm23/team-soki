package soki.textadventure.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class GameController {   // Controller for soki-game.fxml

    public void QUITGame(ActionEvent actionEvent) {
        Platform.exit(); // CLOSES ALL STAGES
    }

    // TODO: Possibility to quit game and/or return to menu

}
