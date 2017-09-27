package gsutils.service;

import gsutils.gscore.GSGameEvent;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by mspellecacy on 8/18/2017.
 */
public enum OLEDRotationService {
    INSTANCE;

    private final Logger log = LoggerFactory.getLogger(OLEDRotationService.class);
    private final OLEDEventRotationService oledEventRotationService = new OLEDEventRotationService();
    private final GameSenseService gameSenseService = GameSenseService.INSTANCE;
    private final PreferencesService preferencesService = PreferencesService.INSTANCE;

    OLEDRotationService() {
        init();
    }

    public void registerGameEvent(String gameEvent) {
        if (!oledEventRotationService.eventOutputOptions.contains(gameEvent)) {
            log.info("Registering OLED Event: {}", gameEvent);
            oledEventRotationService.eventOutputOptions.add(gameEvent);
            log.info("Current Output Options: {}", oledEventRotationService.eventOutputOptions);
        }
    }

    public void unregisterGameEvent(String gameEvent) {
        log.debug("Unregistering OLED Event: {}", gameEvent);
        oledEventRotationService.eventOutputOptions.remove(gameEvent);
    }

    public void queueOLEDEvent(GSGameEvent event) {
        oledEventRotationService.oledEventQueue.add(event);
        log.debug("Queued Event: {} QueueSize {}", new Object[]{event.getEvent(), oledEventRotationService.oledEventQueue.size()});

    }

    //TODO: Seem kinda redundant? Better to let the service manage itself over giving direct access to controllers.
    public int getIntervalSeconds() {
        return oledEventRotationService.getOledRotationIntervalSeconds();
    }

    public void setIntervalSeconds(int interval) {
        oledEventRotationService.setOledRotationIntervalSeconds(interval);
    }

    private void init() {
        log.info("OLED Rotation Service Starting...");

        //Set our rotation interval based on saved preferences, otherwise give a default value of 3 seconds.
        if (preferencesService.getUserPrefs().getOledRotationInterval() != null) {
            oledEventRotationService.setOledRotationIntervalSeconds(preferencesService.getUserPrefs().getOledRotationInterval());
        } else {
            oledEventRotationService.setOledRotationIntervalSeconds(3);
        }

        oledEventRotationService.setOnFailed(e -> {
            log.info("OLED Rotation Failed: {}", e.getSource().getMessage());
            oledEventRotationService.restart();
        });

        oledEventRotationService.start();
    }

    private class OLEDEventRotationService extends Service<Void> {

        final ArrayBlockingQueue<GSGameEvent> oledEventQueue = new ArrayBlockingQueue<>(10);
        final List<String> eventOutputOptions = new ArrayList<>();
        private int oledRotationIntervalSeconds = 3;
        private LocalDateTime lastRotationTime = LocalDateTime.now();

        public int getOledRotationIntervalSeconds() {
            return oledRotationIntervalSeconds;
        }

        public void setOledRotationIntervalSeconds(int oledRotationIntervalSeconds) {
            this.oledRotationIntervalSeconds = oledRotationIntervalSeconds;
        }

        protected Task<Void> createTask() {

            Thread.currentThread().setName("OLED Event Rotation Task");
            return new Task<Void>() {

                @SuppressWarnings("InfiniteLoopStatement")
                protected Void call() {
                    while (true) try {
                        Thread.currentThread().setName("OLED Event Rotation Loop");

                        GSGameEvent e = oledEventQueue.take();
                        if (Objects.equals(e.getEvent(), eventOutputOptions.get(0))) {
                            gameSenseService.sendGameEvent(e);
                        }
                        if (LocalDateTime.now().isAfter(lastRotationTime.plusSeconds(oledRotationIntervalSeconds))) {
                            lastRotationTime = LocalDateTime.now();
                            Collections.rotate(eventOutputOptions, -1);
                        }
                        if (!Objects.equals(e.getEvent(), eventOutputOptions.get(0))) {
                            log.debug("Event Ignored: {}", e.getEvent());
                        }
                    } catch (InterruptedException | IllegalStateException e) {
                        log.info("OLED Event Rotation Service Interrupted: {}", e.getMessage());
                        Thread.currentThread().interrupt();
                        this.cancel();
                    }
                }
            };
        }
    }
}
