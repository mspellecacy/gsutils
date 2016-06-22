package gsutils.service;

import javafx.application.HostServices;

/**
 * Created by mspellecacy on 6/17/2016.
 */

// Lifted and adapted from:
// http://stackoverflow.com/questions/33094981/javafx-8-open-a-link-in-a-browser-without-reference-to-application Solution #4
public enum HostServicesService {
    INSTANCE ;

    private HostServices hostServices ;
    public void init(HostServices hostServices) {
        if (this.hostServices != null) {
            throw new IllegalStateException("Host services already initialized");
        }
        this.hostServices = hostServices ;
    }
    public HostServices getHostServices() {
        if (hostServices == null) {
            throw new IllegalStateException("Host services not initialized");
        }
        return hostServices ;
    }
}
