package gsutils.core;

/**
 * Created by mspellecacy on 6/11/2016.
 */
public class UserPreferences {
    private String gameSenseEndpoint;
    private Boolean systemStatsOn;
    private String systemStatsString;
    private String openWeatherMapApiKey;
    private String weatherZipcodeString;

    public UserPreferences() {
    }

    public UserPreferences(String gameSenseEndpoint, String systemStatsString, String openWeatherMapApiKey, String weatherZipcodeString) {
        this.gameSenseEndpoint = gameSenseEndpoint;
        this.systemStatsString = systemStatsString;
        this.openWeatherMapApiKey = openWeatherMapApiKey;
        this.weatherZipcodeString = weatherZipcodeString;
    }

    public String getGameSenseEndpoint() {
        return gameSenseEndpoint;
    }

    public void setGameSenseEndpoint(String gameSenseEndpoint) {
        this.gameSenseEndpoint = gameSenseEndpoint;
    }

    public Boolean getSystemStatsOn() {
        return systemStatsOn;
    }

    public void setSystemStatsOn(Boolean systemStatsOn) {
        this.systemStatsOn = systemStatsOn;
    }

    public String getSystemStatsString() {
        return systemStatsString;
    }

    public void setSystemStatsString(String systemStatsString) {
        this.systemStatsString = systemStatsString;
    }

    public String getOpenWeatherMapApiKey() {
        return openWeatherMapApiKey;
    }

    public void setOpenWeatherMapApiKey(String openWeatherMapApiKey) {
        this.openWeatherMapApiKey = openWeatherMapApiKey;
    }

    public String getWeatherZipcodeString() {
        return weatherZipcodeString;
    }

    public void setWeatherZipcodeString(String weatherZipcodeString) {
        this.weatherZipcodeString = weatherZipcodeString;
    }
}
