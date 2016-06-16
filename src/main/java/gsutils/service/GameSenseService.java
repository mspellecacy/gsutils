package gsutils.service;

import gsutils.gscore.GSBindEvent;
import gsutils.gscore.GSEventRegistration;
import gsutils.gscore.GSGameRegistration;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;

/**
 * Created by mspellecacy on 6/6/2016.
 */
public class GameSenseService {

    public static String GAMESENSE_HOST = "http://127.0.0.1:52083";

    public static String GAME_EVENT_PATH = "/game_event";

    public static String REGISTER_GAME_PATH = "/game_metadata";

    public static String REGISTER_GAME_EVENT_PATH = "/register_game_event";

    public static String BIND_GAME_EVENT_PATH = "/bind_game_event";

    private ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(GameSenseService.class);

    public GameSenseService() {
    }

    public GameSenseService(String gameSenseHost) {
        GAMESENSE_HOST = gameSenseHost;
    }

    public Boolean registerGame(GSGameRegistration gsGameRegistration) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        Boolean postSuccess = false;

        try {
            HttpPost post = new HttpPost(GAMESENSE_HOST + REGISTER_GAME_PATH);
            StringEntity jsonPayload = new StringEntity(mapper.writeValueAsString(gsGameRegistration));
            log.info("Game Registration JSON: "+mapper.writeValueAsString(gsGameRegistration));
            post.addHeader("content-type", "application/json");
            post.setEntity(jsonPayload);

            //Push Game Registration POST
            HttpResponse response = httpClient.execute(post);

            //Cleanup...
            post.releaseConnection();

            //All went well enough.
            postSuccess = true;

            log.info("Response Body: "+EntityUtils.toString(response.getEntity()));

        } catch (IOException ex) {
            log.error("Error sending game registration: "+ex.getMessage());
        }

        return postSuccess;
    }

    public Boolean sendGameEvent(String jsonGameEvent) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        Boolean postSuccess = false;

        try {
            HttpPost post = new HttpPost(GAMESENSE_HOST + GAME_EVENT_PATH);
            StringEntity jsonPayload = new StringEntity(jsonGameEvent);
            post.addHeader("content-type", "application/json");
            post.setEntity(jsonPayload);

            HttpResponse response = httpClient.execute(post);

            log.info("Response Body: "+EntityUtils.toString(response.getEntity()));

            postSuccess = true;

        } catch (Exception ex) {
            log.error("Error sending game event: "+ex.getMessage());
        }

        return postSuccess;

    }

    public Boolean bindGameEvent(String jsonBindEvent) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        Boolean postSuccess = false;

        try {
            HttpPost post = new HttpPost(GAMESENSE_HOST + BIND_GAME_EVENT_PATH);

            StringEntity jsonPayload = new StringEntity(jsonBindEvent);
            post.addHeader("content-type", "application/json");
            post.setEntity(jsonPayload);

            HttpResponse response = httpClient.execute(post);

            //System.out.println(response);
            log.info("Response Body: "+EntityUtils.toString(response.getEntity()));
            postSuccess = true;
        } catch (Exception ex) {
            log.error("Error sending bind game event: "+ex.getMessage());
        }

        return postSuccess;
    }

    public Boolean bindGameEvent(GSBindEvent bindEvent) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        Boolean postSuccess = false;

        try {
            HttpPost post = new HttpPost(GAMESENSE_HOST + BIND_GAME_EVENT_PATH);
            StringEntity jsonPayload = new StringEntity(mapper.writeValueAsString(bindEvent));
            post.addHeader("content-type", "application/json");
            post.setEntity(jsonPayload);

            HttpResponse response = httpClient.execute(post);

            //System.out.println(response);
            log.info("Response Body: "+EntityUtils.toString(response.getEntity()));
            postSuccess = true;
        } catch (Exception ex) {
            log.error("Error sending bind game event: "+ex.getMessage());
        }

        return postSuccess;
    }


    //TODO: Implement /register_game_event... a somewhat redundant endpoint, since it can all be done with bind.
    public boolean registerGameEvent(GSEventRegistration gsEventRegistration) {

        HttpClient httpClient = HttpClientBuilder.create().build();
        Boolean postSuccess = false;

        try {
            HttpPost post = new HttpPost(GAMESENSE_HOST + REGISTER_GAME_EVENT_PATH);
            StringEntity jsonPayload = new StringEntity(mapper.writeValueAsString(gsEventRegistration));
            log.info("Event Registration JSON: "+mapper.writeValueAsString(gsEventRegistration));
            post.addHeader("content-type", "application/json");
            post.setEntity(jsonPayload);

            //Push Game Registration POST
            HttpResponse response = httpClient.execute(post);

            //Cleanup...
            post.releaseConnection();

            //All went well enough.
            postSuccess = true;

            log.info("Response Body: "+EntityUtils.toString(response.getEntity()));

        } catch (IOException ex) {
            log.error("Error sending game registration: "+ex.getMessage());
        }

        return postSuccess;
    }

}
