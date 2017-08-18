package gsutils.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import gsutils.core.QueuedEvent;
import gsutils.gscore.GSBindEvent;
import gsutils.gscore.GSEventRegistration;
import gsutils.gscore.GSGameEvent;
import gsutils.gscore.GSGameRegistration;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by mspellecacy on 6/6/2016.
 * TODO: This Service doesn't feel very DRY.
 * TODO: Implement a proper HTTP connection manager instead of just relying on the default manager to shut them down.
 */

public enum GameSenseService {
    INSTANCE;

    private static final Logger log = LoggerFactory.getLogger(GameSenseService.class);
    private static final BlockingQueue<QueuedEvent> gsEventsQueue = new ArrayBlockingQueue<>(10);

    private static final String GAME_EVENT_PATH = "/game_event";
    private static final String REGISTER_GAME_PATH = "/game_metadata";
    private static final String REGISTER_GAME_EVENT_PATH = "/register_game_event";
    private static final String BIND_GAME_EVENT_PATH = "/bind_game_event";
    private static final String REMOVE_EVENT_PATH = "/remove_game_event";
    private static final String REMOVE_GAME_PATH = "/remove_game";
    private final GSEventService gsEventService = new GSEventService();
    private final ObjectMapper mapper = new ObjectMapper();

    private String gameSenseHost = "http://127.0.0.1:52083";

    GameSenseService() {
        mapper.findAndRegisterModules();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        //This was the most elegant way to selectively ignore @JsonTypeInfo annotations when serializing that I could find.
        //It basically just returns null when the @JsonTypeInfo is found in a class during serialization.
        //Lifted/Adapted from: http://jackson-users.ning.com/forum/topics/how-to-not-include-type-info-during-serialization-with
        final JacksonAnnotationIntrospector serializeAnnotationIntrospector = new JacksonAnnotationIntrospector() {
            protected TypeResolverBuilder<?> _findTypeResolver(MapperConfig<?> config, Annotated ann, JavaType baseType) {
                if (!ann.hasAnnotation(JsonTypeInfo.class)) {
                    return super._findTypeResolver(config, ann, baseType);
                }
                return null;
            }
        };

        mapper.setAnnotationIntrospector(serializeAnnotationIntrospector);

        //Setup our basic event queue service
        gsEventService.setPeriod(Duration.seconds(2));
        gsEventService.start();

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

    // TODO: Implement Quality-of-Life Utilities Menu for doing app maintenance against the GameSense Host
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

    public void queueGameEvent(GSGameEvent event, long delay) {
        QueuedEvent qe = new QueuedEvent(event, delay);  // 1 second
        if (!gsEventsQueue.contains(qe))
            gsEventsQueue.offer(qe);
    }

    public void queueGameEvent(GSGameEvent event) {
        QueuedEvent qe = new QueuedEvent(event, 1000);  // 1 second
        if (!gsEventsQueue.contains(qe))
            gsEventsQueue.offer(qe);
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
            post.releaseConnection();
        } catch (Exception ex) {
            log.error("Error sending game event: {}", ex.getMessage());
        }

        return postSuccess;
    }

    public Boolean bindGameEvent(GSBindEvent bindEvent) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        Boolean postSuccess = false;
        final ObjectWriter w = mapper.writer();

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
            log.info("Event Removal JSON: {}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(gsEventRegistration));
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

    private class GSEventService extends ScheduledService<Void> {

        // TODO? This seems entirely too coupled and brittle.
        // We create a simple list of our output options way outside of the task scope.
        private final List<String> outputRotation = Arrays.asList("DATETIME", "SYSTEM", "WEATHER");

        protected Task<Void> createTask() {
            return new Task<Void>() {
                protected Void call() throws Exception {
                    log.debug("Current time: {} Queue Size: {}", new Object[]{LocalDateTime.now(), gsEventsQueue.size()});
                    ArrayList<QueuedEvent> queuedEvents = new ArrayList<>();
                    gsEventsQueue.drainTo(queuedEvents);

                    for (QueuedEvent qe : queuedEvents) {
                        GSGameEvent event = qe.getEvent();
                        log.debug("Event: " + event.getEvent() + " Payload: " + event.getData().get("value"));
                        if (Objects.equals(event.getEvent(), outputRotation.get(0))) {
                            sendGameEvent(event);
                            Collections.rotate(outputRotation, -1); //Rotate our output option
                            break; // break out of this loop, ready to start anew.
                        }
                    }
                    return null;
                }
            };

        }
    }

}
