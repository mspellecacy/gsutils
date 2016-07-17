package gsutils.gscore;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by mspellecacy on 7/10/2016.
 * NOTES: In order to avoid Type Erasure, we must inform jackson that we want it to embed class information when
 * serializing and how to retrieve that embedded class information when we deserialize it.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GSPatternCustom.class),
        @JsonSubTypes.Type(value = GSPatternPredefined.class),
        @JsonSubTypes.Type(value = GSRangePattern.class),
        @JsonSubTypes.Type(value = GSRangeRepeatLimit.class)
})
public interface GSPattern {
}
