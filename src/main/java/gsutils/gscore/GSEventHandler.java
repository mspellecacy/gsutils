package gsutils.gscore;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by mspellecacy on 6/14/2016.
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GSTactileEventHandler.class, name = "GSTactileEventHandler"),
        @JsonSubTypes.Type(value = GSScreenedEventHandler.class, name = "GSScreenedEventHandler")
})
public interface GSEventHandler {

    GSDeviceType getDeviceType();

    GSDeviceZone getZone();

}
