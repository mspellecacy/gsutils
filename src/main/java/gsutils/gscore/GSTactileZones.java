package gsutils.gscore;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by mspellecacy on 6/14/2016.
 */
public enum GSTactileZones implements GSDeviceZone {

    //I guess 'zones' is a bit dramatic here.
    ONE();

    private final String zoneName;

    GSTactileZones() {
        this.zoneName = "one";
    }

    @JsonValue
    @Override
    public String toString() {
        return this.zoneName;
    }

}
