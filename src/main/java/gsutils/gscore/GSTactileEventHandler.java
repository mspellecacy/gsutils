package gsutils.gscore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by mspellecacy on 7/9/2016.
 */
@JsonPropertyOrder({"device-type", "mode", "zone", "pattern", "rate"})
public class GSTactileEventHandler implements GSEventHandler {

    @JsonProperty("mode")
    private String mode;

    //This is really the meat.
    private GSPattern[] pattern;
    private Integer rate;

    public GSTactileEventHandler() {
    }

    @JsonProperty("device-type")
    @Override
    public GSDeviceType getDeviceType() {
        return GSDeviceType.TACTILE;
    }

    @JsonProperty("zone")
    @Override
    public GSDeviceZone getZone() {
        return GSTactileZones.ONE;
    }

    public String getMode() {
        return "vibrate";
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @JsonProperty("pattern")
    public GSPattern[] getPattern() {
        return pattern;
    }

    public void setPattern(GSPattern[] pattern) {
        this.pattern = pattern;
    }


}