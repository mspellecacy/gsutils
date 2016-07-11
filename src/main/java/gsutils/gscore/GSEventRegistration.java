package gsutils.gscore;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by mspellecacy on 6/12/2016.
 */
public class GSEventRegistration {

    private String game;
    private String event;

    @JsonProperty("min_value")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer minValue;

    @JsonProperty("max_value")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer maxValue;

    @JsonProperty("icon_id")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Integer iconId;

    public GSEventRegistration() {
    }

    public GSEventRegistration(String game, String event) {
        this.game = game;
        this.event = event;
    }

    public GSEventRegistration(String game, String event, Integer minValue, Integer maxValue, Integer iconId) {
        this.game = game;
        this.event = event;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.iconId = iconId;
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

    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    public Integer getIconId() {
        return iconId;
    }

    public void setIconId(Integer iconId) {
        this.iconId = iconId;
    }
}
