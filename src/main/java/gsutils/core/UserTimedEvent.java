package gsutils.core;

import gsutils.gscore.GSBindEvent;

import java.time.LocalDateTime;

/**
 * Created by mspellecacy on 7/10/2016.
 */
public class UserTimedEvent {
    private GSBindEvent gameEvent;
    private LocalDateTime nextTriggerDateTime;
    private Boolean autoRestartTimer;
    private Integer repeat;
    private Integer interval;  //In Seconds...
    private Boolean enabled;
    private String eventName;

    public UserTimedEvent() {
    }

    public UserTimedEvent(GSBindEvent gameEvent, LocalDateTime nextTriggerDateTime) {
        this.gameEvent = gameEvent;
        this.nextTriggerDateTime = nextTriggerDateTime;
    }

    public UserTimedEvent(GSBindEvent gameEvent, LocalDateTime nextTriggerDateTime, Boolean autoRestartTimer) {
        this.gameEvent = gameEvent;
        this.nextTriggerDateTime = nextTriggerDateTime;
        this.autoRestartTimer = autoRestartTimer;
    }

    public UserTimedEvent(GSBindEvent gameEvent, LocalDateTime nextTriggerDateTime, Boolean autoRestartTimer, Integer repeat) {
        this.gameEvent = gameEvent;
        this.nextTriggerDateTime = nextTriggerDateTime;
        this.autoRestartTimer = autoRestartTimer;
        this.repeat = repeat;
    }

    public UserTimedEvent(GSBindEvent gameEvent, LocalDateTime nextTriggerDateTime, Boolean autoRestartTimer, Integer repeat, Integer interval) {
        this.gameEvent = gameEvent;
        this.nextTriggerDateTime = nextTriggerDateTime;
        this.autoRestartTimer = autoRestartTimer;
        this.repeat = repeat;
        this.interval = interval;
        this.enabled = true;
    }

    public UserTimedEvent(GSBindEvent gameEvent, LocalDateTime nextTriggerDateTime, Boolean autoRestartTimer, Integer repeat, Integer interval, Boolean enabled) {
        this.gameEvent = gameEvent;
        this.nextTriggerDateTime = nextTriggerDateTime;
        this.autoRestartTimer = autoRestartTimer;
        this.repeat = repeat;
        this.interval = interval;
        this.enabled = enabled;
    }

    public GSBindEvent getGameEvent() {
        return gameEvent;
    }

    public void setGameEvent(GSBindEvent gameEvent) {
        this.gameEvent = gameEvent;
    }

    public LocalDateTime getNextTriggerDateTime() {
        return nextTriggerDateTime;
    }

    public void setNextTriggerDateTime(LocalDateTime nextTriggerDateTime) {
        this.nextTriggerDateTime = nextTriggerDateTime;
    }

    public Boolean getAutoRestartTimer() {
        return autoRestartTimer;
    }

    public void setAutoRestartTimer(Boolean autoRestartTimer) {
        this.autoRestartTimer = autoRestartTimer;
    }

    public Integer getRepeat() {
        return repeat;
    }

    public void setRepeat(Integer repeat) {
        this.repeat = repeat;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getEventName() {
        return this.gameEvent.getEvent();
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
        this.gameEvent.setEvent(eventName);
    }

}
