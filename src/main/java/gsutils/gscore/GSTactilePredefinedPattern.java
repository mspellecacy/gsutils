package gsutils.gscore;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Created by mspellecacy on 7/10/2016.
 * TODO: Implement all the current predefined patterns: https://github.com/SteelSeries/gamesense-sdk/blob/master/doc/api/json-handlers-tactile.md#reference-sections---ti-predefined-vibrations
 * (Low Hanging Fruit) LHF: Shouldn't take more than a couple macro runs over the markdown list.
 */
public enum GSTactilePredefinedPattern {
    STRONGCLICK_100("ti_predefined_strongclick_100");

    private String patternName;

    GSTactilePredefinedPattern(String patternName) { this.patternName = patternName; }

    @JsonValue
    @Override
    public String toString() {
        return this.patternName;
    }

}
