package gsutils.gscore;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by mspellecacy on 7/10/2016.
 * NOTES: Docs seem to indicate only tactile has any predefined patterns, so further abstraction isn't (yet?) needed.
 */
public class GSPatternPredefined implements GSPattern {

    private GSTactilePredefinedPattern type;
    private Integer delay_ms;

    public GSPatternPredefined() {
    }

    public GSPatternPredefined(GSTactilePredefinedPattern type) {
        this.type = type;
    }

    public GSPatternPredefined(GSTactilePredefinedPattern type, Integer delay_ms) {
        this.type = type;
        this.delay_ms = delay_ms;
    }

    public GSTactilePredefinedPattern getType() {
        return type;
    }

    public void setType(GSTactilePredefinedPattern type) {
        this.type = type;
    }

    @JsonProperty("delay-ms")
    public Integer getDelay_ms() {
        return delay_ms;
    }

    public void setDelay_ms(Integer delay_ms) {
        this.delay_ms = delay_ms;
    }
}
