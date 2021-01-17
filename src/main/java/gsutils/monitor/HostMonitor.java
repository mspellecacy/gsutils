package gsutils.monitor;

import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Set;

import static javax.management.ObjectName.getInstance;

/**
 * Created by mspellecacy on 6/6/2016.
 */
public class HostMonitor {
    private static final Logger log = LoggerFactory.getLogger(HostMonitor.class);
    @SuppressWarnings("CanBeFinal")
    private static MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    private static OperatingSystemMXBean altbean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
            .getOperatingSystemMXBean();
    @SuppressWarnings("CanBeFinal")
    private static ObjectName osAttrs;

    static {
        try {
            osAttrs = getInstance("java.lang:type=OperatingSystem");
        } catch (MalformedObjectNameException e) {
            log.error("Where are we?");
        }
    }

    /**
     * @return Percentage of System Memory Usage
     */
    public static Double getMemoryUsedPercent() {
        return ((double) getMemoryUsed() / (double) getTotalMemory()) * 100;
    }

    /**
     * @return Consumed System Memory in Bytes
     */
    public static Long getMemoryUsed() {
        return getTotalMemory() - getMemoryFree();
    }

    /**
     * @return Free System Memory in Bytes
     */
    public static Long getMemoryFree() {

        AttributeList list = new AttributeList();
        try {
            list = mbs.getAttributes(osAttrs, new String[]{"FreePhysicalMemorySize"});
        } catch (InstanceNotFoundException | ReflectionException e) {
            e.printStackTrace();
        }

        if (list.isEmpty()) return Long.MIN_VALUE;

        Attribute att = (Attribute) list.get(0);

        return (Long) att.getValue();
    }

    /**
     * @return Total System Memory in Bytes
     */
    public static Long getTotalMemory() {

        AttributeList list = new AttributeList();
        try {
            list = mbs.getAttributes(osAttrs, new String[]{"TotalPhysicalMemorySize"});
        } catch (InstanceNotFoundException | ReflectionException e) {
            e.printStackTrace();
        }

        if (list.isEmpty()) return Long.MIN_VALUE;

        Attribute att = (Attribute) list.get(0);

        return (Long) att.getValue();

    }

    //Parts lifted directly from StackOverflow: http://stackoverflow.com/questions/18489273/how-to-get-percentage-of-cpu-usage-of-os-from-java
    /**
     * @return System's CPU load in percentage
     */
    public static double getSystemCpuLoad() {

        AttributeList list = new AttributeList();
        try {
            list = mbs.getAttributes(osAttrs, new String[]{"SystemCpuLoad"});
        } catch (InstanceNotFoundException | ReflectionException e) {
            e.printStackTrace();
        }

        if (list.isEmpty()) return Double.NaN;

        Attribute att = (Attribute) list.get(0);
        Double value = (Double) att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0) return Double.NaN;
        // returns a percentage value with 1 decimal point precision
        return ((int) (value * 1000) / 10.0);
    }

    public static double getSystemCpuLoadAlt() {
        double value = altbean.getSystemCpuLoad();
        return ((int) (value * 1000) / 10.0);
    }

    public static String getSystemArch() {
        return altbean.getArch();
    }

    public static String getOsName() {
        return altbean.getName();
    }

    public static double getLoadAverageAlt() {
        return altbean.getSystemLoadAverage();
    }

    public static double getCpuLoadAverage() {
        AttributeList list = new AttributeList();
        try {
            list = mbs.getAttributes(osAttrs, new String[]{"SystemLoadAverage"});
        } catch (InstanceNotFoundException | ReflectionException e) {
            e.printStackTrace();
        }

        if (list.isEmpty()) return Double.NaN;

        Attribute att = (Attribute) list.get(0);
        Double value = (Double) att.getValue();

        // usually takes a couple of seconds before we get real values
        if (value == -1.0) return Double.NaN;
        // returns a percentage value with 1 decimal point precision
        return ((int) (value * 1000) / 10.0);
    }

    public static void debugMBeanAttributes() {
        Set mbeans = mbs.queryNames(null, null);
        for (Object mbean : mbeans) {
            try {
                WriteAttributes(mbs, (ObjectName) mbean);
            } catch (InstanceNotFoundException | IntrospectionException | ReflectionException e) {
                e.printStackTrace();
            }
        }
    }

    private static void WriteAttributes(final MBeanServer mBeanServer, final ObjectName http)
            throws InstanceNotFoundException, IntrospectionException, ReflectionException {
        MBeanInfo info = mBeanServer.getMBeanInfo(http);
        MBeanAttributeInfo[] attrInfo = info.getAttributes();

        log.info("Attributes for object: " + http + ":\n");
        for (MBeanAttributeInfo attr : attrInfo) {
            log.info("  " + attr.getName() + "\n");
        }
    }

}
