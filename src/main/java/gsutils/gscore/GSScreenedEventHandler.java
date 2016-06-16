package gsutils.gscore;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by mspellecacy on 6/14/2016.
 */
public class GSScreenedEventHandler implements GSEventHandler {

    @JsonProperty("device-type")
    @Override
    public GSDeviceType getDeviceType() {
        return GSDeviceType.SCREENED;
    }

    @JsonProperty("zone")
    @Override
    public GSDeviceZone getZone() {
        return GSScreenedZones.ONE;
    }

    private static final String mode = "screen";

    //This is really the meat.
    private Map<String, String> datas;


    public GSScreenedEventHandler() {
    }

    public static String getMode() {
        return mode;
    }

    @JsonProperty("datas")
    public Map<String, String> getDatas() {
        return datas;
    }

    public void setDatas(Map<String, String> datas) {
        this.datas = datas;
    }


}
