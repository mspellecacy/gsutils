package gsutils.service;

/**
 * Created by mspellecacy on 6/17/2016.
 */
public class HostServicesService {
    private static HostServicesService ourInstance = new HostServicesService();

    public static HostServicesService getInstance() {
        return ourInstance;
    }

    private HostServicesService() {
    }
}
