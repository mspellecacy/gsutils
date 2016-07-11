package gsutils.gscore;

/**
 * Created by mspellecacy on 7/10/2016.
 */
public class GSRangeRepeatLimit {

    private Integer low;
    private Integer high;
    private Integer rate_limit;

    public GSRangeRepeatLimit() {
    }

    public GSRangeRepeatLimit(Integer low, Integer high, Integer rate_limit) {
        this.low = low;
        this.high = high;
        this.rate_limit = rate_limit;
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

    public Integer getRate_limit() {
        return rate_limit;
    }

    public void setRate_limit(Integer rate_limit) {
        this.rate_limit = rate_limit;
    }

}
