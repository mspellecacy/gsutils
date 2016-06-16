package gsutils.gscore;

/**
 * Created by mspellecacy on 6/14/2016.
 */
public enum GSDeviceType {
    //TODO: Implement these device zones.
    //General Types
    KEYBOARD("Keyboard"),
    MOUSE("Mouse"),
    HEADSET("Headset"),
    INDICATOR("Indicator"),

    //TODO: Implement these zones.
    //RGB Zones
    RGB_1_ZONE("rgb-1-zone"),
    RGB_2_ZONE("rgb-1-zone"),
    RGB_3_ZONE("rgb-1-zone"),
    //WUT?: No 4 in spec? Re: https://github.com/SteelSeries/gamesense-sdk/blob/master/doc/api/standard-zones.md
    RGB_5_ZONE("rgb-1-zone"),
    RGB_PER_KEY_ZONES("rgb-per-key-zones"),

    //Misc. (What I actually need for my Rival 700. :D )
    TACTILE("tactile"),
    SCREENED("screened");

    private String deviceTypeName;

    GSDeviceType(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    @Override
    public String toString() {
        return this.deviceTypeName;
    }


}

