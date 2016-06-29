package gsutils.core;

import gsutils.monitor.WeatherMonitor;

/**
 * Created by mspellecacy on 6/11/2016.
 */
public class UserPreferences {
    private String gameSenseEndpoint;
    private Boolean systemStatsOn;
    private String systemStatsString;
    private String openWeatherMapApiKey;
    private String weatherZipcodeString;
    private String weatherStatsString;
    private Boolean runWeatherMonitor;
    private WeatherMonitor.WeatherUnit weatherUnit;
    private Boolean runDatetime;
    private String datetimePattern;

    public UserPreferences() {
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

    public Boolean getRunWeatherMonitor() {
        return runWeatherMonitor;
    }

    public void setRunWeatherMonitor(Boolean runWeatherMonitor) {
        this.runWeatherMonitor = runWeatherMonitor;
    }

    public String getWeatherStatsString() {
        return weatherStatsString;
    }

    public void setWeatherStatsString(String weatherStatsString) {
        this.weatherStatsString = weatherStatsString;
    }

    public WeatherMonitor.WeatherUnit getWeatherUnit() {
        return weatherUnit;
    }

    public void setWeatherUnit(WeatherMonitor.WeatherUnit weatherUnit) {
        this.weatherUnit = weatherUnit;
    }

    public Boolean getRunDatetime() {
        return runDatetime;
    }

    public void setRunDatetime(Boolean runDatetime) {
        this.runDatetime = runDatetime;
    }

    public String getDatetimePattern() {
        return datetimePattern;
    }

    public void setDatetimePattern(String datetimePattern) {
        this.datetimePattern = datetimePattern;
    }
}
