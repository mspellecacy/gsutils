package gsutils.gscore;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;

/**
 * Created by mspellecacy on 6/14/2016.
 */
@JsonPropertyOrder({"device-type", "mode", "zone", "datas"})
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

    //This is really the meat.
    private ArrayList datas;


    public GSScreenedEventHandler() {
    }

    public String getMode() {
        return "screen";
    }

    @JsonProperty("datas")
    public ArrayList getDatas() {
        return datas;
    }

    public void setDatas(ArrayList datas) {
        this.datas = datas;
    }


}
