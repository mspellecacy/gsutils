package gsutils.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gsutils.core.UserPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mspellecacy on 6/11/2016.
 */
public class PreferencesService {
    private static PreferencesService instance;
    private static final Logger log = LoggerFactory.getLogger(PreferencesService.class);
    private static final String FILE_SEP = System.getProperty("file.separator");
    private static final String USER_HOME = System.getProperty("user.home");
    private static final String PREFS_DIR = ".gsutils";
    private static final String PREFS_FILE = "preferences.json";

    private ObjectMapper mapper = new ObjectMapper();
    private UserPreferences userPrefs;
    private File prefsFile;


    private PreferencesService() {
        loadPreferences();
    }

    public static synchronized PreferencesService getInstance() {
        if (instance == null) {
            instance = new PreferencesService();
        }
        return instance;
    }

    private Boolean loadPreferences() {
        boolean loadedSuccessfully;
        String dir = USER_HOME + FILE_SEP + PREFS_DIR + FILE_SEP;
        File configDir = new File(dir);
        prefsFile = new File(dir + PREFS_FILE);

        //See if we have a config dir...
        if (!configDir.isDirectory()) {
            log.info("Config Dir not found, trying to create create: "+configDir.getAbsolutePath());
            if (configDir.mkdir()) {
                log.info("Config dir created.");
            } else {
                //TODO: Add graceful exit for this...
                log.info("Unable to create config dir: ");
                loadedSuccessfully = false;
            }
        }

        log.info("User Preferences Path: " + prefsFile.getPath());
        log.info("Opening user preferences...");
        try {
            //If we created a new file, write an empty preferences file.

            if (prefsFile.createNewFile()) {
                userPrefs = new UserPreferences();
                mapper.writeValueAsString(userPrefs);
                // If we did not create a new file load our existing preferences.
            } else userPrefs = mapper.readValue(prefsFile, UserPreferences.class);
            loadedSuccessfully = true;
        } catch (IOException e) {
            log.error("File Save Error : " + e.getMessage());
            userPrefs = new UserPreferences();
            loadedSuccessfully = false;
        }

        return loadedSuccessfully;
    }

    public UserPreferences getUserPrefs() {
        return userPrefs;
    }

    public void savePreferences(){
        try {
            mapper.writeValue(prefsFile, userPrefs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO: I handle this poorly, and we end up reading the props file too much. Needs a refactor in to sanity.
    // Make GameSenseService a singleton? OMGz DEH ANTI-PATTERNS. D: D: D:
    public String getGameSenseEndpoint() {

        String endpoint = "";

        //Lifted and adapted from: https://github.com/SteelSeries/gamesense-sdk/blob/master/examples/minecraftforge1.8/src/main/java/com/sse3/gamesense/GameSenseMod.java#L137
        String PRGDATA = System.getenv("PROGRAMDATA");
        File propsFile = new File( PRGDATA+FILE_SEP+"SteelSeries"+FILE_SEP+"SteelSeries Engine 3"+FILE_SEP+"coreProps.json" );

        try {
            Map<String, Object> confObj = mapper.readValue(propsFile, new TypeReference<Map<String, Object>>() {});
            endpoint = (String) confObj.get("address");

        } catch (IOException e) {
            log.error("Error loading GameSense Config: {}", e.getMessage());
        }

        return endpoint;
    }

    public void setUserPrefs(UserPreferences userPrefs) {
        this.userPrefs = userPrefs;
    }
}

