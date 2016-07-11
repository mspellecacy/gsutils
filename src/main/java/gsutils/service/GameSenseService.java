package gsutils.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import gsutils.gscore.GSBindEvent;
import gsutils.gscore.GSEventRegistration;
import gsutils.gscore.GSGameEvent;
import gsutils.gscore.GSGameRegistration;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by mspellecacy on 6/6/2016.
 */

//TODO: This Service doesn't feel very DRY.
public enum GameSenseService {
    INSTANCE;

    private static final Logger log = LoggerFactory.getLogger(GameSenseService.class);

    private static String GAME_EVENT_PATH = "/game_event";
    private static String REGISTER_GAME_PATH = "/game_metadata";
    private static String REGISTER_GAME_EVENT_PATH = "/register_game_event";
    private static String BIND_GAME_EVENT_PATH = "/bind_game_event";
    private static String REMOVE_EVENT_PATH = "/remove_game_event";
    private static String REMOVE_GAME_PATH = "/remove_game";

    private String gameSenseHost = "http://127.0.0.1:52083";

    private ObjectMapper mapper = new ObjectMapper();

    GameSenseService() {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public String getGameSenseHost() {
        return gameSenseHost;
    }

    public void setGameSenseHost(String host) {
        gameSenseHost = host;
    }

    public Boolean registerGame(GSGameRegistration gsGameRegistration) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        Boolean postSuccess = false;

        try {
            HttpPost post = new HttpPost(gameSenseHost + REGISTER_GAME_PATH);
            StringEntity jsonPayload = new StringEntity(mapper.writeValueAsString(gsGameRegistration));
            log.info("Game Registration JSON: {} ", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(gsGameRegistration));
            post.addHeader("content-type", "application/json");
            post.setEntity(jsonPayload);

            HttpResponse response = httpClient.execute(post);

            post.releaseConnection();

            postSuccess = true;
            log.debug("Response Body: {}", EntityUtils.toString(response.getEntity()));

        } catch (IOException ex) {
            log.error("Error sending game registration: {}", ex.getMessage());
        }

        return postSuccess;
    }

    public Boolean unregisterGame(GSGameRegistration gsGameRegistration) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        Boolean postSuccess = false;

        try {
            HttpPost post = new HttpPost(gameSenseHost + REMOVE_GAME_PATH);
            StringEntity jsonPayload = new StringEntity(mapper.writeValueAsString(gsGameRegistration));
            log.info("Game Removal JSON: {} ", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(gsGameRegistration));
            post.addHeader("content-type", "application/json");
            post.setEntity(jsonPayload);

            HttpResponse response = httpClient.execute(post);

            post.releaseConnection();

            postSuccess = true;
            log.debug("Response Body: {}", EntityUtils.toString(response.getEntity()));

        } catch (IOException ex) {
            log.error("Error sending game registration: {}", ex.getMessage());
        }

        return postSuccess;
    }

    public Boolean sendGameEvent(GSGameEvent gsGameEvent) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        Boolean postSuccess = false;

        try {
            HttpPost post = new HttpPost(gameSenseHost + GAME_EVENT_PATH);
            StringEntity jsonPayload = new StringEntity(mapper.writeValueAsString(gsGameEvent));
            log.info("Event Payload: {}", mapper.writeValueAsString(gsGameEvent)); //PrettyPrint gets obnoxious here.
            post.addHeader("content-type", "application/json");
            post.setEntity(jsonPayload);

            HttpResponse response = httpClient.execute(post);

            log.debug("Response Body: {}", EntityUtils.toString(response.getEntity()));
            postSuccess = true;
        } catch (Exception ex) {
            log.error("Error sending game event: {}", ex.getMessage());
        }

        return postSuccess;
    }

    public Boolean bindGameEvent(GSBindEvent bindEvent) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        Boolean postSuccess = false;

        try {
            HttpPost post = new HttpPost(gameSenseHost + BIND_GAME_EVENT_PATH);
            StringEntity jsonPayload = new StringEntity(mapper.writeValueAsString(bindEvent));
            log.info("Bind Event JSON: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bindEvent));
            post.addHeader("content-type", "application/json");
            post.setEntity(jsonPayload);

            HttpResponse response = httpClient.execute(post);

            log.debug("Response Body: {}", EntityUtils.toString(response.getEntity()));

            EntityUtils.consume(response.getEntity());
            postSuccess = true;
        } catch (Exception ex) {
            log.error("Error sending bind game event: {}", ex.getMessage());
        }

        return postSuccess;
    }

    public boolean registerGameEvent(GSEventRegistration gsEventRegistration) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        Boolean postSuccess = false;

        try {
            HttpPost post = new HttpPost(gameSenseHost + REGISTER_GAME_EVENT_PATH);
            StringEntity jsonPayload = new StringEntity(mapper.writeValueAsString(gsEventRegistration));
            log.info("Event Registration JSON: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(gsEventRegistration));
            post.addHeader("content-type", "application/json");
            post.setEntity(jsonPayload);

            //Push Game Registration POST
            HttpResponse response = httpClient.execute(post);

            //Cleanup...
            post.releaseConnection();

            //All went well enough.
            log.debug("Response Body: {}", EntityUtils.toString(response.getEntity()));
            postSuccess = true;
        } catch (IOException ex) {
            log.error("Error sending game registration: {}", ex.getMessage());
        }

        return postSuccess;
    }

    public boolean removeGameEvent(GSEventRegistration gsEventRegistration) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        Boolean postSuccess = false;

        try {
            HttpPost post = new HttpPost(gameSenseHost + REMOVE_EVENT_PATH);
            StringEntity jsonPayload = new StringEntity(mapper.writeValueAsString(gsEventRegistration));
            log.info("Event Registration JSON: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(gsEventRegistration));
            post.addHeader("content-type", "application/json");
            post.setEntity(jsonPayload);

            //Push Game Registration POST
            HttpResponse response = httpClient.execute(post);

            //Cleanup...
            post.releaseConnection();

            //All went well enough.
            log.debug("Response Body: {}", EntityUtils.toString(response.getEntity()));
            postSuccess = true;
        } catch (IOException ex) {
            log.error("Error sending game registration: {}", ex.getMessage());
        }

        return postSuccess;
    }

}
