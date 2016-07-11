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
    private Map<String, Object> data;

    public GSGameEvent() {
    }

    public GSGameEvent(String game, String event) {
        this.game = game;
        this.event = event;
    }

    public GSGameEvent(String game, String event, Map<String, Object> data) {
        this.game = game;
        this.event = event;
        this.data = data;
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

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
