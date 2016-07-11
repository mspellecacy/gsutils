package gsutils.gscore;

/**
 * Created by mspellecacy on 7/10/2016.
 */
public class GSRangePattern implements GSPattern {

    private Integer low;
    private Integer high;
    private GSPattern[] pattern;

    public GSRangePattern() {
    }

    public GSRangePattern(Integer low, Integer high, GSPattern[] pattern) {
        this.low = low;
        this.high = high;
        this.pattern = pattern;
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

    public GSPattern[] getPattern() {
        return pattern;
    }

    public void setPattern(GSPattern[] pattern) {
        this.pattern = pattern;
    }
}
