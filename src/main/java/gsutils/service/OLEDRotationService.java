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

    private final OLEDEventRotationService oledEventRotationService = new OLEDEventRotationService();
    private final GameSenseService gameSenseService = GameSenseService.INSTANCE;
    private final Logger log = LoggerFactory.getLogger(OLEDRotationService.class);

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

    private void init() {
        log.info("OLED Rotation Service Starting...");

        oledEventRotationService.setOnFailed(e -> {
            log.info("OLED Rotation Failed...restarting...");
            oledEventRotationService.restart();
        });

        oledEventRotationService.start();
    }

    private class OLEDEventRotationService extends Service<Void> {

        final ArrayBlockingQueue<GSGameEvent> oledEventQueue = new ArrayBlockingQueue<>(10);
        final List<String> eventOutputOptions = new ArrayList<>();
        private final int oledRotationIntervalSeconds = 3;
        private LocalDateTime lastRotationTime = LocalDateTime.now();

        protected Task<Void> createTask() {
            Thread.currentThread().setName("OLED Event Rotation Task.");

            return new Task<Void>() {

                @SuppressWarnings("InfiniteLoopStatement")
                protected Void call() {
                    while (true) try {
                        GSGameEvent e = oledEventQueue.take();

                        if (Objects.equals(e.getEvent(), eventOutputOptions.get(0))) {
                            gameSenseService.sendGameEvent(e);
                        }
                        if (LocalDateTime.now().isAfter(lastRotationTime.plusSeconds(oledRotationIntervalSeconds))) {
                            lastRotationTime = LocalDateTime.now();
                            Collections.rotate(eventOutputOptions, -1);
                        }
                        if (!Objects.equals(e.getEvent(), eventOutputOptions.get(0))) {
                            log.debug("{} Event Ignored.", e.getEvent());
                        }
                    } catch (InterruptedException ignored) {
                        // The process will be restarted.
                    }
                }
            };
        }
    }
}
