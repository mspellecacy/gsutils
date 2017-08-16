package gsutils.gscore;

/**
 * Created by mspellecacy on 7/10/2016.
 */
public class GSRangeFrequency {

    private Integer low;
    private Integer high;
    private Integer frequency; //FIXME: Docs seem to indicate this can be an additional 'recursive'(?) frequency def.

    public GSRangeFrequency() {
    }

    public GSRangeFrequency(Integer low, Integer high, Integer frequency) {
        this.low = low;
        this.high = high;
        this.frequency = frequency;
    }

    public Integer getLow() {
        return low;
    }

    public void setLow(Integer low) {
        this.low = low;
    }

    public Integer getHigh() {
        return high;
    }

    public void setHigh(Integer high) {
        this.high = high;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }
}

