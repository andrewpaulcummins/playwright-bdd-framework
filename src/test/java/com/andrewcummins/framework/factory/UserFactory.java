package com.andrewcummins.framework.factory;

import com.andrewcummins.framework.models.User;
import com.andrewcummins.framework.utils.JsonDataReader;

/**
 * Factory class responsible for creating {@code User} objects from test data.
 *
 * <p>This class acts as the bridge between the Cucumber feature file step
 * definitions and the encrypted test data stored in {@code users.json}.
 * It accepts a user type string (e.g. {@code "standard"}, {@code "locked"})
 * and delegates to {@code JsonDataReader} to load, decrypt, and return
 * the corresponding {@code User} object.</p>
 *
 * <h2>Role in the dynamic step pattern</h2>
 * <p>The dynamic step pattern allows a single Gherkin step to handle all
 * user types:</p>
 * <pre>
 *   Given a "standard" user is on the "login" page
 *   Given a "locked" user is on the "login" page
 * </pre>
 * <p>The user type string captured from the feature file is passed directly
 * to {@link #getUser(String)}, keeping the step definition clean and the
 * test data lookup fully abstracted.</p>
 *
 * <h2>Fail fast</h2>
 * <p>If an unrecognised user type is passed, {@code JsonDataReader} throws
 * a descriptive exception listing all valid user types. This surfaces
 * feature file mistakes immediately rather than causing silent failures
 * or misleading errors in later steps.</p>
 */
public class UserFactory {

    /**
     * The {@code JsonDataReader} instance used to load and decrypt user data.
     */
    private final JsonDataReader jsonDataReader;

    /**
     * Constructs a new {@code UserFactory} with the given {@code JsonDataReader}.
     *
     * <p>The {@code JsonDataReader} is provided by {@code ScenarioContext},
     * ensuring the test data file is only loaded and parsed once per scenario
     * rather than on every user lookup.</p>
     *
     * @param jsonDataReader the data reader providing access to decrypted user data
     */
    public UserFactory(JsonDataReader jsonDataReader) {
        this.jsonDataReader = jsonDataReader;
    }

    /**
     * Returns a fully decrypted {@code User} object for the given user type.
     *
     * <p>The user type string is matched case-insensitively against the keys
     * in {@code users.json}. This means {@code "Standard"}, {@code "STANDARD"},
     * and {@code "standard"} all resolve to the same user, making feature files
     * more forgiving of capitalisation differences.</p>
     *
     * <p>If the user type is not found in the test data, a descriptive
     * {@code RuntimeException} is thrown immediately listing all available
     * user types, allowing the issue to be identified and fixed quickly.</p>
     *
     * @param userType the user type key from the feature file step
     *                 (e.g. "standard", "locked", "problem", "performance_glitch")
     * @return a fully populated and decrypted {@code User} object
     * @throws RuntimeException if the user type is null, empty, or not found
     *                          in the test data file
     */
    public User getUser(String userType) {
        validateUserType(userType);
        return jsonDataReader.getUser(userType.toLowerCase().trim());
    }

    /**
     * Validates that the user type string is not null or empty before lookup.
     *
     * <p>This guards against feature file steps that accidentally pass an
     * empty string or null, producing a clear error message rather than
     * a confusing {@code NullPointerException} inside {@code JsonDataReader}.</p>
     *
     * @param userType the user type string to validate
     * @throws RuntimeException if the user type is null or empty
     */
    private void validateUserType(String userType) {
        if (userType == null || userType.trim().isEmpty()) {
            throw new RuntimeException(
                    "[UserFactory] User type cannot be null or empty. " +
                            "Check the feature file step — the user type should be " +
                            "a non-empty string in double quotes, e.g. \"standard\"."
            );
        }
    }
}