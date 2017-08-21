package gsutils.gscore;

/**
 * Created by mspellecacy on 7/9/2016.
 */
class GSRate {
    private Integer frequency;
    private Integer repeat_limit;

    public GSRate() {
    }

    public GSRate(Integer frequency, Integer repeat_limit) {
        this.frequency = frequency;
        this.repeat_limit = repeat_limit;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Integer getRepeat_limit() {
        return repeat_limit;
    }

    public void setRepeat_limit(Integer repeat_limit) {
        this.repeat_limit = repeat_limit;
    }
}
