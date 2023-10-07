package config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private int maxRequestsCount;
    private int terminationMinutesForEmptyQueue;
    private int terminationTimeForTheSameTask;
    private int maxInstancesCount;
    private int cellCalculationThreadCount;

    public Config() {
        var properties = getProperties();
        maxRequestsCount = Integer.parseInt(properties.getProperty("maxRequestsCount"));
        terminationMinutesForEmptyQueue = Integer.parseInt(properties.getProperty("terminationMinutesForEmptyQueue"));
        terminationTimeForTheSameTask = Integer.parseInt(properties.getProperty("terminationTimeForTheSameTask"));
        maxInstancesCount = Integer.parseInt(properties.getProperty("maxInstancesCount"));
        cellCalculationThreadCount = Integer.parseInt(properties.getProperty("cellCalculationThreadCount"));
    }
    public int getMaxRequestsCount() {
        return maxRequestsCount;
    }
    public int getTerminationMinutesForEmptyQueue() {
        return terminationMinutesForEmptyQueue;
    }
    public int getTerminationTimeForTheSameTask() {
        return terminationTimeForTheSameTask;
    }
    public int getMaxInstancesCount() {
        return maxInstancesCount;
    }
    public int getCellCalculationThreadCount() {
        return cellCalculationThreadCount;
    }
    public Properties getProperties(){
        try {

            FileReader reader = new FileReader(System.getProperty("user.dir") + "/src/main/java/config/msArt.properties");
            Properties props = new Properties();
            props.load(reader);
            return props;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
