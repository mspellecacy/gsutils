package gsutils.monitor;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mspellecacy on 6/12/2016.
 */
public class WeatherMonitor {

    private static final Logger log = LoggerFactory.getLogger(WeatherMonitor.class);
    private static final String OWM_API = "http://api.openweathermap.org/data/2.5/weather";

    private final ObjectMapper mapper = new ObjectMapper();
    private String owmApiKey;

    public WeatherMonitor(String owmApiKey) {
        this.owmApiKey = owmApiKey;
    }

    public String getOwmApiKey() {
        return owmApiKey;
    }

    public void setOwmApiKey(String owmApiKey) {
        this.owmApiKey = owmApiKey;
    }

    public Map<String, Object> getWeatherByZip(String zipcode, WeatherUnit unit){

        Map<String, Object> weatherObj = new HashMap<>();

        HttpClient httpClient = HttpClientBuilder.create().build();
        String zipQuery = OWM_API+"?units="+unit+"&zip="+zipcode+"&APPID="+owmApiKey;
        HttpGet zipQueryGet = new HttpGet(zipQuery);

        try {
            HttpResponse response = httpClient.execute(zipQueryGet);
            String respBody = EntityUtils.toString(response.getEntity());
            weatherObj = mapper.readValue(respBody, new TypeReference<Map<String, Object>>() {});
            log.debug("API Response: " + respBody);
        } catch (IOException e) {
            log.error("Error in Fetch: "+e.getMessage());
        }

        return weatherObj;
    }


    public enum WeatherUnit {
        STANDARD("kelvin"),
        METRIC("metric"),
        IMPERIAL("imperial");

        private final String unitName;

        WeatherUnit(String deviceTypeName) {
            this.unitName = deviceTypeName;
        }

        @JsonValue
        @Override
        public String toString() {
            return this.unitName;
        }

    }
}
