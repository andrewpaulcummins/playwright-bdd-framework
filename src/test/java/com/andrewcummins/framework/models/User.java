package com.andrewcummins.framework.models;

/**
 * Represents a test user loaded from the encrypted test data file.
 *
 * <p>This model is populated by {@code JsonDataReader} after decrypting
 * the values from {@code users.json}. It is then passed through the
 * framework via {@code ScenarioContext} and used in step definitions
 * and page interactions.</p>
 *
 * <p>All sensitive fields (username, password) are decrypted before
 * being set on this object. By the time a {@code User} instance exists
 * in memory, its values are plain text and ready for use.</p>
 */
public class User {

    private String username;
    private String password;
    private String role;

    /**
     * Default no-argument constructor required for Jackson deserialisation.
     *
     * <p>Jackson needs a no-arg constructor to instantiate the object
     * before setting field values via setters.</p>
     */
    public User() {}

    /**
     * Constructs a fully populated User with all fields set.
     *
     * @param username the decrypted username for this user
     * @param password the decrypted password for this user
     * @param role     the role label identifying this user type (e.g. "standard", "locked")
     */
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    /**
     * Returns the decrypted username for this user.
     *
     * @return the username string
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for this user.
     *
     * @param username the username string to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the decrypted password for this user.
     *
     * @return the password string
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for this user.
     *
     * @param password the password string to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the role label for this user.
     *
     * <p>The role is used throughout the framework to identify user types
     * in logging, reporting, and conditional test logic. Unlike username
     * and password, the role is not encrypted as it contains no
     * sensitive information.</p>
     *
     * @return the role string (e.g. "standard", "locked", "problem")
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role for this user.
     *
     * @param role the role string to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns a string representation of this user for logging and debugging.
     *
     * <p>Note that the password is intentionally masked in this output.
     * Never log plain text passwords — even in a test framework.</p>
     *
     * @return a formatted string showing the username and role, with password masked
     */
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='[MASKED]'" +
                ", role='" + role + '\'' +
                '}';
    }
}