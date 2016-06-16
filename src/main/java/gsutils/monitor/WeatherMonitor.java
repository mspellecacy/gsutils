package gsutils.monitor;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by mspellecacy on 6/12/2016.
 */
public class WeatherMonitor {

    private static final Logger log = LoggerFactory.getLogger(WeatherMonitor.class);
    private static final String OWM_API = "http://api.openweathermap.org/data/2.5/weather";

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

    public String getWeatherByZip(String zipcode, WeatherUnit unit){
        HttpClient httpClient = HttpClientBuilder.create().build();
        String zipQuery = OWM_API+"?units="+unit+"&zip="+zipcode+"&APPID="+owmApiKey;
        HttpGet zipQueryGet = new HttpGet(zipQuery);

        try {
            HttpResponse response = httpClient.execute(zipQueryGet);
            log.info(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return"50F";
    }


    public enum WeatherUnit {
        STANDARD(""),
        METRIC("metric"),
        IMPERIAL("imperial");

        private String unitName;

        WeatherUnit(String deviceTypeName) {
            this.unitName = deviceTypeName;
        }

        @Override
        public String toString() {
            return this.unitName;
        }

    }
}
