module TeamSoki.Textadventure {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires json.simple;
    requires java.desktop;

    // https://www.tutorialguruji.com/java/illegalaccessexception-cannot-access-class-c-in-module-m-because-module-m-does-not-export-c-to-module-n/
    opens soki.textadventure.controller to javafx.fxml;
    exports soki.textadventure.controller;

    // TODO: Still needed?
    opens soki.textadventure to javafx.fxml;

    exports soki.textadventure;
}