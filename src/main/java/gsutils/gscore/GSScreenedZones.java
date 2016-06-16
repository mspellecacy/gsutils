package gsutils.gscore;

/**
 * Created by mspellecacy on 6/14/2016.
 */
public enum GSScreenedZones implements GSDeviceZone {

    //I guess 'zones' is a bit dramatic here.
    ONE("one");

    private String zoneName;

    GSScreenedZones(String zoneName) {
        this.zoneName = zoneName;
    }

    @Override
    public String toString() {
        return this.zoneName;
    }


}
