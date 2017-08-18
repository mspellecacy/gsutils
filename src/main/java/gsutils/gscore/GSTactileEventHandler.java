package gsutils.gscore;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created by mspellecacy on 7/9/2016.
 */
@JsonPropertyOrder({"device-type", "mode", "zone", "pattern", "rate"})
public class GSTactileEventHandler implements GSEventHandler {

    //This is really the meat.
    private GSPattern[] pattern;
    private Integer rate;
    private GSDeviceType deviceType;
    private String mode;
    private GSTactileZones zone;

    public GSTactileEventHandler() {
    }

    @JsonGetter("device-type")
    public GSDeviceType getDeviceType() {
        return GSDeviceType.TACTILE;
    }

    @JsonGetter("zone")
    public GSDeviceZone getZone() {
        return GSTactileZones.ONE;
    }

    public String getMode() {
        return "vibrate";
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public GSPattern[] getPattern() {
        return pattern;
    }

    public void setPattern(GSPattern[] pattern) {
        this.pattern = pattern;
    }


}