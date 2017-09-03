package gsutils;

import gsutils.gscore.GSGameRegistration;
import gsutils.service.GameSenseService;
import gsutils.service.HostServicesService;
import gsutils.service.PreferencesService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainApp extends Application {

    private static final String APP_NAME = "GSUtils";
    private static final String BUILD_NAME = "Moby Dick";
    private static final Image APP_ICON = new Image(MainApp.class.getResourceAsStream("/images/gsutils.png"));
    private static final Logger log = LoggerFactory.getLogger(MainApp.class);


    private final GameSenseService gsService = GameSenseService.INSTANCE;
    private final PreferencesService prefsService = PreferencesService.INSTANCE;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        log.info("Starting GSUtils");

        //First we must setup our endpoint.
        String gsEndpoint = "HTTP://" + prefsService.getGameSenseEndpoint();
        gsService.setGameSenseHost(gsEndpoint);
        log.info("GameSense Endpoint: {}", gsEndpoint);

        //Second thing, register our 'game' with the GameSense Engine.
        GSGameRegistration gameRegistration = new GSGameRegistration();
        gameRegistration.setGame("GSUTILS");
        gameRegistration.setGameDisplayName("GSUtils");
        gameRegistration.setIconColorId(2);
        gsService.registerGame(gameRegistration);

        //Load up our FXML index
        String fxmlFile = "/fxml/index.fxml";
        log.debug("Loading FXML for main view from: {}", fxmlFile);
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = loader.load(getClass().getResourceAsStream(fxmlFile));

        //Finally build and show our scene on the main stage.
        log.debug("Showing JFX scene");
        Scene scene = new Scene(rootNode, 800, 600);
        scene.getStylesheets().add("/styles/styles.css");
        stage.getIcons().add(APP_ICON);
        stage.setTitle(APP_NAME + " - Build: " + BUILD_NAME + " - GameSense Endpoint: " + gsService.getGameSenseHost());
        stage.setScene(scene);
        stage.show();

        // Setups a way for us to open the user's browser for showing them docs.
        HostServicesService.INSTANCE.init(getHostServices());


        //When the main app stage closes, shut down all the things. ALL THE THINGS!
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

    }
}
