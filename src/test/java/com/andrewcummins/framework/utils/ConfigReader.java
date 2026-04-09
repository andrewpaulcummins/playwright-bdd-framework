package com.andrewcummins.framework.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides centralised, type-safe access to the framework's configuration properties.
 *
 * <p>Loads {@code config.properties} from the classpath at instantiation and exposes
 * each configuration value through a dedicated method. Identical in design to the
 * Selenium framework's ConfigReader, updated for Playwright specific properties.</p>
 */
public class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private final Properties properties;

    /**
     * Constructs a new {@code ConfigReader} and immediately loads {@code config.properties}.
     *
     * @throws RuntimeException if the configuration file cannot be found or read
     */
    public ConfigReader() {
        this.properties = new Properties();
        loadProperties();
    }

    /**
     * Returns the browser to use for Playwright initialisation.
     *
     * <p>Supported values: {@code chromium}, {@code firefox}, {@code webkit}</p>
     *
     * @return the browser name in lowercase
     */
    public String getBrowser() {
        String systemProperty = System.getProperty("browser");
        if (systemProperty != null && !systemProperty.trim().isEmpty()) {
            return systemProperty.toLowerCase().trim();
        }
        return getRequiredProperty("browser").toLowerCase().trim();
    }

    /**
     * Returns whether the browser should run in headless mode.
     *
     * @return {@code true} if headless mode is enabled
     */
    public boolean isHeadless() {
        // Allow CI/CD to override via Maven system property -Dheadless=true
        String systemProperty = System.getProperty("headless");
        if (systemProperty != null && !systemProperty.trim().isEmpty()) {
            return Boolean.parseBoolean(systemProperty.trim());
        }
        return Boolean.parseBoolean(getRequiredProperty("headless"));
    }

    /**
     * Returns the slow motion delay in milliseconds between Playwright actions.
     *
     * <p>Set to 0 for normal speed. Useful for visual debugging.</p>
     *
     * @return the slow motion delay in milliseconds
     */
    public int getSlowMotion() {
        return getIntProperty("slow.motion");
    }

    /**
     * Returns the default timeout for Playwright actions and assertions in milliseconds.
     *
     * @return the default timeout in milliseconds
     */
    public int getDefaultTimeout() {
        return getIntProperty("default.timeout");
    }

    /**
     * Returns the navigation timeout in milliseconds.
     *
     * @return the navigation timeout in milliseconds
     */
    public int getNavigationTimeout() {
        return getIntProperty("navigation.timeout");
    }

    /**
     * Returns the base URL for the UI application under test.
     *
     * @return the UI base URL string
     */
    public String getUiBaseUrl() {
        return getRequiredProperty("ui.base.url");
    }

    /**
     * Returns the base URL for the primary REST API under test.
     *
     * @return the API base URL string
     */
    public String getApiBaseUrl() {
        return getRequiredProperty("api.base.url");
    }

    /**
     * Returns the base URL for the secondary REST API under test.
     *
     * @return the secondary API base URL string
     */
    public String getApiSecondaryUrl() {
        return getRequiredProperty("api.secondary.url");
    }

    /**
     * Returns the ReqRes API key used for the {@code x-api-key} request header.
     *
     * <p>Can be overridden at runtime via {@code -Dapi.key=<value>} (e.g. from a
     * CI secret) without modifying {@code config.properties}.</p>
     *
     * @return the API key string
     */
    public String getApiKey() {
        String systemProperty = System.getProperty("api.key");
        if (systemProperty != null && !systemProperty.trim().isEmpty()) {
            return systemProperty.trim();
        }
        return getRequiredProperty("api.key");
    }

    /**
     * Returns the classpath-relative path to the encrypted test data JSON file.
     *
     * @return the test data file path string
     */
    public String getTestDataPath() {
        return getRequiredProperty("test.data.path");
    }

    /**
     * Returns whether screenshots should be captured automatically on test failure.
     *
     * @return {@code true} if failure screenshots are enabled
     */
    public boolean isScreenshotOnFailure() {
        return Boolean.parseBoolean(getRequiredProperty("screenshot.on.failure"));
    }

    /**
     * Returns the directory path where failure screenshots are saved.
     *
     * @return the screenshot output directory path
     */
    public String getScreenshotPath() {
        return getRequiredProperty("screenshot.path");
    }

    /**
     * Returns the Playwright trace mode.
     *
     * <p>Supported values: {@code on}, {@code off}, {@code retain-on-failure}</p>
     *
     * @return the trace mode string
     */
    public String getTraceMode() {
        return getRequiredProperty("trace.mode");
    }

    /**
     * Returns the directory path where Playwright trace files are saved.
     *
     * @return the trace output directory path
     */
    public String getTracePath() {
        return getRequiredProperty("trace.path");
    }

    /**
     * Returns the Allure results output directory path.
     *
     * @return the Allure results directory path
     */
    public String getAllureResultsDir() {
        return getRequiredProperty("allure.results.dir");
    }

    /**
     * Returns the configured log level for framework output.
     *
     * @return the log level string
     */
    public String getLogLevel() {
        return getRequiredProperty("log.level");
    }

    /**
     * Loads {@code config.properties} from the classpath.
     *
     * @throws RuntimeException if the file is not found or cannot be read
     */
    private void loadProperties() {
        try {
            InputStream inputStream = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(CONFIG_FILE);

            if (inputStream == null) {
                throw new RuntimeException(
                        "[ConfigReader] Configuration file '" + CONFIG_FILE + "' was not " +
                                "found on the classpath. Ensure it exists at " +
                                "src/test/resources/config.properties."
                );
            }

            properties.load(inputStream);

        } catch (RuntimeException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException(
                    "[ConfigReader] Failed to read configuration file '" + CONFIG_FILE +
                            "'. Error: " + e.getMessage(), e
            );
        }
    }

    /**
     * Retrieves a required property value by key, failing fast if absent.
     *
     * @param key the property key to look up
     * @return the trimmed property value string
     * @throws RuntimeException if the property is missing or empty
     */
    private String getRequiredProperty(String key) {
        String value = properties.getProperty(key);

        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(
                    "[ConfigReader] Required property '" + key + "' is missing or empty " +
                            "in '" + CONFIG_FILE + "'."
            );
        }

        return value.trim();
    }

    /**
     * Retrieves a required property value and converts it to an {@code int}.
     *
     * @param key the property key to look up
     * @return the property value as an {@code int}
     * @throws RuntimeException if the property is missing or not a valid integer
     */
    private int getIntProperty(String key) {
        String value = getRequiredProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException(
                    "[ConfigReader] Property '" + key + "' has value '" + value +
                            "' which cannot be parsed as an integer."
            );
        }
    }
}