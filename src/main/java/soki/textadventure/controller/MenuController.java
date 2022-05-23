package soki.textadventure.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import soki.textadventure.Main;

import java.io.IOException;
import java.util.Objects;

public class MenuController {
    // Controller for soki-menu.fxml
    private FileController fileController = new FileController();

    public void quitGameMenu(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }

    public void LoadGameMenu(ActionEvent actionEvent) throws IOException {
        // Closes Menu window --> would still exist, if Game is started
        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();

        // TODO: Load existing game
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/soki-game.fxml"));
        Parent rootGame = fxmlLoader.load();
        Scene gameScene = new Scene(rootGame);
        Stage gameStage = new Stage();
        gameStage.setTitle("SOKI");
        // gameStage.initStyle(StageStyle.TRANSPARENT); // remove close, minimize, full screen option
        // gameStage.setFullScreen(true); // full screen
        gameStage.setResizable(false); // cannot change window size
        gameStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // cannot exit full screen
        gameStage.setScene(gameScene);
        gameStage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/icons/Soki-Icon.png"))));

        gameStage.show();
    }

    public void openNewGame(ActionEvent actionEvent) throws IOException {
        // Closes Menu window --> would still exist, if Game is started
        fileController.initializePlayerdata();
        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/soki-game.fxml"));
        Parent rootGame = fxmlLoader.load();
        Scene gameScene = new Scene(rootGame);
        Stage gameStage = new Stage();
        gameStage.setTitle("SOKI");
        // gameStage.initStyle(StageStyle.TRANSPARENT); // remove close, minimize, full screen option
        // gameStage.setFullScreen(true); // full screen
        gameStage.setResizable(false); // cannot change window size
        gameStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // cannot exit full screen
        gameStage.setScene(gameScene);
        gameStage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/icons/Soki-Icon.png"))));

        gameStage.show();
    }
}
