package gsutils.core;

import gsutils.gscore.GSGameEvent;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

class QueuedEvent implements Delayed {

    private final long origin;
    private final long delay;
    private GSGameEvent event;

    public QueuedEvent(GSGameEvent event, final long delay) {
        this.origin = System.currentTimeMillis();
        this.delay = delay;
        this.event = event;
    }


    public GSGameEvent getEvent() {
        return event;
    }

    public void setEvent(GSGameEvent event) {
        this.event = event;
    }

    @Override
    public int hashCode() {
        final int prime = 31;

        int result = 1;
        result = prime * result + ((event == null) ? 0 : event.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof QueuedEvent)) {
            return false;
        }

        final QueuedEvent other = (QueuedEvent) obj;
        if (event == null) {
            if (other.event != null) {
                return false;
            }
        } else if (!event.equals(other.event)) {
            return false;
        }

        return true;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(delay - (System.currentTimeMillis() - origin), TimeUnit.MILLISECONDS);

    }

    @Override
    public int compareTo(Delayed delayed) {
        if (delayed == this) {
            return 0;
        }

        if (delayed instanceof QueuedEvent) {
            long diff = delay - ((QueuedEvent) delayed).delay;
            return ((diff == 0) ? 0 : ((diff < 9) ? -1 : 1));
        }

        long d = (getDelay(TimeUnit.MILLISECONDS) - delayed.getDelay(TimeUnit.MILLISECONDS));
        return ((d == 0) ? 0 : ((d < 0) ? -1 : 1));

    }
}
