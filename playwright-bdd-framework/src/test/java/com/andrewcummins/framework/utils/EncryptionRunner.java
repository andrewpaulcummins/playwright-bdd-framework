package com.andrewcummins.framework.utils;

/**
 * One-time utility runner for generating encrypted values to store in users.json.
 *
 * <p>This class is NOT part of the test framework itself. It is a developer
 * tool used to encrypt plain-text credentials before they are stored in the
 * JSON test data files. Run this once when setting up new test data, copy
 * the encrypted output into users.json, then you are done.</p>
 *
 * <h2>How to run</h2>
 * <ol>
 *   <li>Ensure the TEST_DATA_SECRET_KEY environment variable is set</li>
 *   <li>Right click this file in IntelliJ and select "Run EncryptionRunner.main()"</li>
 *   <li>Copy the encrypted values from the console output</li>
 *   <li>Paste them into the appropriate fields in users.json</li>
 * </ol>
 *
 * <h2>Important</h2>
 * <p>Never commit plain-text passwords to source control. Always encrypt first
 * using this runner, then store only the encrypted output in users.json.</p>
 */
public class EncryptionRunner {

    public static void main(String[] args) {

        System.out.println("=================================================");
        System.out.println("  ENCRYPTION RUNNER — Generating encrypted values");
        System.out.println("=================================================");
        System.out.println();

        // The plain-text values we want to encrypt.
        // Add any new credentials here before running.
        String[] valuesToEncrypt = {
                "secret_sauce",         // SauceDemo password — used by all user types
                "standard_user",        // standard user username
                "locked_out_user",      // locked user username
                "problem_user",         // problem user username
                "performance_glitch_user" // performance glitch user username
        };

        System.out.println("Encrypting values using key from TEST_DATA_SECRET_KEY...");
        System.out.println();

        for (String value : valuesToEncrypt) {
            String encrypted = EncryptionUtil.encrypt(value);
            String decrypted = EncryptionUtil.decrypt(encrypted);

            System.out.println("Plain text  : " + value);
            System.out.println("Encrypted   : " + encrypted);
            System.out.println("Verified    : " + decrypted);
            System.out.println("Match       : " + value.equals(decrypted));
            System.out.println("-------------------------------------------------");
        }

        System.out.println();
        System.out.println("Copy the encrypted values above into users.json.");
        System.out.println("=================================================");
    }
}