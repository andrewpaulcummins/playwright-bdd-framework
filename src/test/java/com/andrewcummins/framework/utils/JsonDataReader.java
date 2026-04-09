package com.andrewcummins.framework.utils;

import com.andrewcummins.framework.models.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

/**
 * Reads and decrypts test data from the encrypted JSON test data files.
 *
 * <p>This class is the single point of entry for all test data in the framework.
 * It is responsible for:</p>
 * <ul>
 *   <li>Locating and loading the JSON test data file from the classpath</li>
 *   <li>Parsing the JSON structure using Jackson's {@code ObjectMapper}</li>
 *   <li>Decrypting sensitive fields (username, password) via {@code EncryptionUtil}</li>
 *   <li>Returning strongly typed {@code User} objects ready for use in tests</li>
 * </ul>
 *
 * <h2>Design decisions</h2>
 * <p>The file is loaded via the ClassLoader rather than a hardcoded file path.
 * This means it works consistently regardless of the operating system, the
 * machine it runs on, or the CI/CD environment — no path separator issues,
 * no "file not found" errors caused by different working directories.</p>
 *
 * <p>Decryption is handled transparently inside this class. The rest of the
 * framework never interacts with encrypted values — by the time a {@code User}
 * object reaches a step definition, all fields are plain text and ready to use.</p>
 *
 * <h2>Fail fast</h2>
 * <p>If the test data file cannot be found, or if a requested user type does
 * not exist in the file, this class throws a descriptive {@code RuntimeException}
 * immediately. This prevents silent failures or misleading NullPointerExceptions
 * deeper in the test execution.</p>
 */
public class JsonDataReader {

    /**
     * Jackson ObjectMapper instance used for parsing JSON.
     *
     * <p>ObjectMapper is thread-safe once configured, so a single instance
     * is reused across all calls rather than instantiating a new one each time.</p>
     */
    private final ObjectMapper objectMapper;

    /**
     * The root JSON node parsed from the test data file.
     *
     * <p>Stored as a field so the file is only read and parsed once when
     * {@code JsonDataReader} is instantiated, rather than on every method call.</p>
     */
    private final JsonNode rootNode;

    /**
     * Constructs a new {@code JsonDataReader} and immediately loads the test data file.
     *
     * <p>The file path is read from {@code config.properties} via {@code ConfigReader}.
     * The file is loaded from the classpath using the thread's context ClassLoader,
     * ensuring consistent behaviour across all environments.</p>
     *
     * @param testDataPath the classpath-relative path to the JSON test data file
     *                     (e.g. "testdata/users.json")
     * @throws RuntimeException if the file cannot be found on the classpath,
     *                          or if the JSON cannot be parsed
     */
    public JsonDataReader(String testDataPath) {
        this.objectMapper = new ObjectMapper();
        this.rootNode = loadFile(testDataPath);
    }

    /**
     * Retrieves a fully decrypted {@code User} object for the given user type.
     *
     * <p>The user type string (e.g. "standard", "locked") is matched against
     * the keys in the "users" object within the JSON file. If found, the
     * encrypted username and password fields are decrypted via {@code EncryptionUtil}
     * and a populated {@code User} object is returned.</p>
     *
     * <p>The role field is not encrypted and is returned as-is from the JSON.</p>
     *
     * @param userType the user type key to look up (e.g. "standard", "locked",
     *                 "problem", "performance_glitch")
     * @return a fully populated and decrypted {@code User} object
     * @throws RuntimeException if the specified user type does not exist in the
     *                          test data file, or if decryption fails
     */
    public User getUser(String userType) {
        JsonNode usersNode = rootNode.get("users");

        if (usersNode == null) {
            throw new RuntimeException(
                    "[JsonDataReader] The test data file does not contain a 'users' " +
                            "object at the root level. Please check the structure of your " +
                            "test data JSON file."
            );
        }

        JsonNode userNode = usersNode.get(userType);

        if (userNode == null) {
            throw new RuntimeException(
                    "[JsonDataReader] User type '" + userType + "' was not found in " +
                            "the test data file. Available user types are: " +
                            getAvailableUserTypes(usersNode) + ". " +
                            "Check your feature file step and users.json for a mismatch."
            );
        }

        String encryptedUsername = getRequiredField(userNode, "username", userType);
        String encryptedPassword = getRequiredField(userNode, "password", userType);
        String role = getRequiredField(userNode, "role", userType);

        String username = EncryptionUtil.decrypt(encryptedUsername);
        String password = EncryptionUtil.decrypt(encryptedPassword);

        return new User(username, password, role);
    }

    /**
     * Loads and parses the JSON test data file from the classpath.
     *
     * <p>Using the ClassLoader to locate the file means Maven's resource
     * filtering and classpath management handle the file location automatically.
     * This works correctly in both local IntelliJ runs and CI/CD pipeline
     * executions without any path configuration.</p>
     *
     * @param testDataPath the classpath-relative path to the JSON file
     * @return the parsed root {@code JsonNode}
     * @throws RuntimeException if the file is not found or cannot be parsed
     */
    private JsonNode loadFile(String testDataPath) {
        try {
            InputStream inputStream = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(testDataPath);

            if (inputStream == null) {
                throw new RuntimeException(
                        "[JsonDataReader] Test data file not found on classpath: '" +
                                testDataPath + "'. " +
                                "Ensure the file exists at src/test/resources/" + testDataPath +
                                " and that Maven has included it in the test classpath."
                );
            }

            return objectMapper.readTree(inputStream);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    "[JsonDataReader] Failed to parse test data file: '" +
                            testDataPath + "'. " +
                            "Ensure the file contains valid JSON. Error: " + e.getMessage(), e
            );
        }
    }

    /**
     * Retrieves a required field value from a JSON node.
     *
     * <p>Fails fast with a descriptive error if the field is missing or null,
     * rather than returning null and causing a NullPointerException elsewhere.</p>
     *
     * @param node      the JSON node to read from
     * @param fieldName the name of the field to retrieve
     * @param userType  the user type context, used in the error message
     * @return the field value as a string
     * @throws RuntimeException if the field is missing or has a null value
     */
    private String getRequiredField(JsonNode node, String fieldName, String userType) {
        JsonNode fieldNode = node.get(fieldName);

        if (fieldNode == null || fieldNode.isNull()) {
            throw new RuntimeException(
                    "[JsonDataReader] Required field '" + fieldName + "' is missing " +
                            "from user type '" + userType + "' in the test data file. " +
                            "Each user entry must contain 'username', 'password', and 'role' fields."
            );
        }

        return fieldNode.asText();
    }

    /**
     * Builds a comma-separated string of available user type keys for use in error messages.
     *
     * <p>This makes the fail-fast error message actionable — the developer sees
     * exactly what user types are available, making it easy to spot a typo in
     * a feature file step.</p>
     *
     * @param usersNode the "users" JSON node containing all user type entries
     * @return a comma-separated string of available user type keys
     */
    private String getAvailableUserTypes(JsonNode usersNode) {
        StringBuilder types = new StringBuilder();
        usersNode.fieldNames().forEachRemaining(name -> {
            if (types.length() > 0) types.append(", ");
            types.append("'").append(name).append("'");
        });
        return types.toString();
    }
}