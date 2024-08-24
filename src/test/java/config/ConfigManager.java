package config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static Properties properties = new Properties();

    static {
        String env = System.getProperty("env", "mock"); // default to mock
        try (InputStream input = ConfigManager.class.getClassLoader()
                .getResourceAsStream("application-" + env + ".properties")) {
            if (input == null) {
                throw new FileNotFoundException("Property file not found for environment: " + env);
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
