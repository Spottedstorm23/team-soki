package soki.textadventure;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import soki.textadventure.controller.FileController;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // ignore - documentation purposes
        // FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("soki-menu.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/soki-menu.fxml"));

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/soki-menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("SOKI");
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT); // remove close, minimize, full screen option
        stage.setFullScreen(true); // full screen
        stage.setResizable(false); // cannot change window size
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // cannot exit full screen
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println("Wir sind cool, miau miau! <(⓿3⓿)>");

        //Nur für Testzwecke
        FileController fc = new FileController();
        String text = fc.getTextFromJson(1,2);
        System.out.println(text);

        fc.writeSavedStatsToFile(1,2);

        //launch();

    }
}
