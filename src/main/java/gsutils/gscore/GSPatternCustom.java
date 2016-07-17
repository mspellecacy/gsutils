package gsutils.gscore;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by mspellecacy on 7/10/2016.
 */
public class GSPatternCustom implements GSPattern {

    private String type;
    private Integer length_ms;
    private Integer delay_ms;

    public GSPatternCustom() {
    }

    public GSPatternCustom(String type, Integer length_ms, Integer delay_ms) {
        this.type = type;
        this.length_ms = length_ms;
        this.delay_ms = delay_ms;
    }

    public String getType() {
        return "custom";  //Because custom
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("length-ms")
    public Integer getLength_ms() {
        return length_ms;
    }

    public void setLength_ms(Integer length_ms) {
        this.length_ms = length_ms;
    }

    @JsonProperty("delay-ms")
    public Integer getDelay_ms() {
        return delay_ms;
    }

    public void setDelay_ms(Integer delay_ms) {
        this.delay_ms = delay_ms;
    }
}
