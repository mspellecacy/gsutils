package gsutils;

import gsutils.gscore.GSGameRegistration;
import gsutils.service.GameSenseService;
import gsutils.service.HostServicesService;
import gsutils.service.PreferencesService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(MainApp.class);

    private final GameSenseService gsService = new GameSenseService();
    private final PreferencesService prefsService = PreferencesService.getInstance();


    //TODO: Refactor all singletons in to enum pattern.
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        log.info("Starting GSUtils");

        //First things first, register our 'game' with the GameSense Engine.
        GSGameRegistration gameRegistration = new GSGameRegistration();
        gameRegistration.setGame("GSUTILS");
        gameRegistration.setGameDisplayName("GSUtils");
        gameRegistration.setIconColorId(2);
        gsService.registerGame(gameRegistration);

        String fxmlFile = "/fxml/index.fxml";
        log.debug("Loading FXML for main view from: {}", fxmlFile);
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = loader.load(getClass().getResourceAsStream(fxmlFile));

        log.debug("Showing JFX scene");
        Scene scene = new Scene(rootNode, 600, 400);
        scene.getStylesheets().add("/styles/styles.css");

        stage.setTitle("GSUtils - Make GameSense Great Again");
        stage.setScene(scene);
        stage.show();

        HostServicesService.INSTANCE.init(getHostServices());

        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

    }
}
