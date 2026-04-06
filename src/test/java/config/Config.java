package config;

import java.io.FileInputStream;
import java.util.Properties;

public class Config {
    private static Properties properties = new Properties();

    static {
        try {
            properties.load(new FileInputStream("src/test/resources/config.properties"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}