package gsutils.gscore;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by mspellecacy on 6/13/2016.
 */
public class GSBindEvent {

    private String game;
    private String event;

    @JsonProperty("max_value")
    private Integer maxValue;

    @JsonProperty("min_value")
    private Integer minValue;

    @JsonProperty("icon_id")
    private Integer iconId;

    @JsonProperty("handlers")
    private GSEventHandler[] eventHandlers;

    public GSBindEvent() {
    }

    public GSBindEvent(String game, String event) {
        this.game = game;
        this.event = event;
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

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public Integer getIconId() {
        return iconId;
    }

    public void setIconId(Integer iconId) {
        this.iconId = iconId;
    }

    public GSEventHandler[] getEventHandlers() {
        return eventHandlers;
    }

    public void setEventHandlers(GSEventHandler[] eventHandlers) {
        this.eventHandlers = eventHandlers;
    }
}
