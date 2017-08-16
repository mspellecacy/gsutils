package gsutils.gscore;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by mspellecacy on 6/14/2016.
 */
public enum GSDeviceType {
    //TODO: Implement General device zones.
    //General Types
    KEYBOARD("Keyboard"),
    MOUSE("Mouse"),
    HEADSET("Headset"),
    INDICATOR("Indicator"),

    //TODO: Implement RGB device zones.
    //RGB Zones
    RGB_1_ZONE("rgb-1-zone"),
    RGB_2_ZONE("rgb-2-zone"),
    RGB_3_ZONE("rgb-3-zone"),
    //WUT?: No 4 in spec? Re: https://github.com/SteelSeries/gamesense-sdk/blob/master/doc/api/standard-zones.md
    RGB_5_ZONE("rgb-5-zone"),
    RGB_PER_KEY_ZONES("rgb-per-key-zones"),

    //Misc. (What I actually need for my Rival 700. :D )
    TACTILE("tactile"),
    SCREENED("screened-128x36");

    private String deviceTypeName;

    GSDeviceType(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    @JsonValue
    @Override
    public String toString() {
        return this.deviceTypeName;
    }


}

