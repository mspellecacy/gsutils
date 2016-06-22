package gsutils.gscore;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by mspellecacy on 6/18/2016.
 */
public class GSGameEvent {

    private String game;
    private String event;

    @JsonProperty("data")
    private Map<String, String> data;

    public GSGameEvent() {
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
