package com.andrewcummins.framework.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utility class providing AES encryption and decryption for sensitive test data.
 *
 * <p>This class is responsible for protecting credentials and other sensitive
 * values stored in the framework's JSON test data files. Data is encrypted
 * before being committed to source control, and decrypted at runtime using
 * a secret key supplied via an environment variable.</p>
 *
 * <h2>How it works</h2>
 * <ol>
 *   <li>A secret key is stored as an environment variable: {@code TEST_DATA_SECRET_KEY}</li>
 *   <li>Test data (e.g. passwords) is encrypted using {@link #encrypt(String)} and
 *       the result is stored in {@code users.json}</li>
 *   <li>At runtime, {@link #decrypt(String)} is called by {@code JsonDataReader}
 *       to restore the original plain-text value before use in tests</li>
 * </ol>
 *
 * <h2>Algorithm</h2>
 * <p>AES/CBC/PKCS5Padding with a 16-byte key and a fixed initialisation vector (IV).
 * AES (Advanced Encryption Standard) is the industry standard symmetric encryption
 * algorithm used across banking, government, and enterprise systems globally.</p>
 *
 * <h2>Security note</h2>
 * <p>The secret key must NEVER be hardcoded in source code or committed to version
 * control. It must always be supplied via the {@code TEST_DATA_SECRET_KEY}
 * environment variable. In CI/CD pipelines (GitHub Actions, Jenkins), this value
 * is stored as a protected secret and injected at runtime.</p>
 */
public class EncryptionUtil {

    /**
     * The name of the environment variable that holds the AES secret key.
     * This variable must be set on any machine that runs the framework,
     * including local developer machines and CI/CD agents.
     */
    private static final String SECRET_KEY_ENV_VAR = "TEST_DATA_SECRET_KEY";

    /**
     * The encryption algorithm, mode, and padding scheme.
     *
     * <ul>
     *   <li><b>AES</b> — Advanced Encryption Standard, symmetric block cipher</li>
     *   <li><b>CBC</b> — Cipher Block Chaining mode, each block depends on the previous</li>
     *   <li><b>PKCS5Padding</b> — standard padding to align data to the block size</li>
     * </ul>
     */
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * The initialisation vector (IV) used for CBC mode encryption.
     *
     * <p>The IV ensures that the same plain-text encrypted twice produces
     * different cipher-text, preventing pattern analysis attacks. It must be
     * exactly 16 bytes for AES. In a production system this would be randomly
     * generated and stored alongside the cipher-text — for a test framework
     * a fixed IV is an acceptable trade-off between security and simplicity.</p>
     */
    private static final String INIT_VECTOR = "encryptionIntVec";

    /**
     * Encrypts a plain-text string using AES/CBC/PKCS5Padding.
     *
     * <p>The result is a Base64-encoded string suitable for storage in a
     * JSON file and safe for inclusion in source control. The original
     * plain-text value cannot be recovered without the secret key.</p>
     *
     * <p>This method is used during test data setup — run it once to encrypt
     * your credentials, then store the output in {@code users.json}.</p>
     *
     * @param plainText the raw string to encrypt (e.g. a password)
     * @return a Base64-encoded encrypted string
     * @throws RuntimeException if encryption fails due to an invalid key,
     *                          missing environment variable, or cipher error
     */
    public static String encrypt(String plainText) {
        try {
            String secretKey = getSecretKey();
            validateKey(secretKey);

            IvParameterSpec iv = new IvParameterSpec(
                    INIT_VECTOR.getBytes(StandardCharsets.UTF_8)
            );
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8), "AES"
            );

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);

            byte[] encrypted = cipher.doFinal(
                    plainText.getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            throw new RuntimeException(
                    "[EncryptionUtil] Failed to encrypt value. " +
                            "Ensure the environment variable '" + SECRET_KEY_ENV_VAR +
                            "' is set to a valid 16-character key. Error: " + e.getMessage(), e
            );
        }
    }

    /**
     * Decrypts a Base64-encoded AES-encrypted string back to its original plain-text value.
     *
     * <p>This method is called at runtime by {@code JsonDataReader} when loading
     * test data from {@code users.json}. The decrypted value is used directly
     * in test execution and is never stored or logged.</p>
     *
     * @param encryptedText the Base64-encoded encrypted string to decrypt
     * @return the original plain-text string
     * @throws RuntimeException if decryption fails due to an invalid key,
     *                          missing environment variable, or corrupted cipher-text
     */
    public static String decrypt(String encryptedText) {
        try {
            String secretKey = getSecretKey();
            validateKey(secretKey);

            IvParameterSpec iv = new IvParameterSpec(
                    INIT_VECTOR.getBytes(StandardCharsets.UTF_8)
            );
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8), "AES"
            );

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);

            byte[] decrypted = cipher.doFinal(
                    Base64.getDecoder().decode(encryptedText)
            );

            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException(
                    "[EncryptionUtil] Failed to decrypt value. " +
                            "Ensure the environment variable '" + SECRET_KEY_ENV_VAR +
                            "' matches the key used during encryption. Error: " + e.getMessage(), e
            );
        }
    }

    /**
     * Retrieves the AES secret key from the environment variable.
     *
     * <p>Fails fast with a clear, descriptive error if the environment variable
     * is not set. This prevents cryptic NullPointerExceptions further down
     * the call stack and makes the root cause immediately obvious.</p>
     *
     * @return the secret key string
     * @throws RuntimeException if the environment variable is not set or is empty
     */
    public static String getSecretKey() {
        String key = System.getenv(SECRET_KEY_ENV_VAR);

        if (key == null || key.trim().isEmpty()) {
            throw new RuntimeException(
                    "[EncryptionUtil] Environment variable '" + SECRET_KEY_ENV_VAR +
                            "' is not set or is empty. " +
                            "This variable must be set on your local machine and in your " +
                            "CI/CD pipeline secrets before running the framework. " +
                            "The key must be exactly 16 characters long."
            );
        }

        return key;
    }

    /**
     * Validates that the secret key meets AES-128 requirements.
     *
     * <p>AES requires a key of exactly 16 bytes (128-bit) for AES-128 encryption.
     * Providing a key of the wrong length will cause the JCE (Java Cryptography
     * Extension) to throw an InvalidKeyException. This validation catches that
     * condition early with a meaningful error message.</p>
     *
     * @param key the secret key to validate
     * @throws RuntimeException if the key is not exactly 16 characters long
     */
    public static void validateKey(String key) {
        if (key.length() != 16) {
            throw new RuntimeException(
                    "[EncryptionUtil] Invalid key length: " + key.length() +
                            " characters. The secret key must be exactly 16 characters " +
                            "(128 bits) for AES-128 encryption."
            );
        }
    }
}