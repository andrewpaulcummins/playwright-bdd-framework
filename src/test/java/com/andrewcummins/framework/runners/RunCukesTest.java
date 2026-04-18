package com.andrewcummins.framework.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Cucumber test runner for the Playwright BDD framework.
 *
 * <p>Uses JUnit 5's {@code @Suite} annotation rather than TestNG's
 * {@code AbstractTestNGCucumberTests}. The {@code @ConfigurationParameter}
 * annotations replace the {@code @CucumberOptions} annotation used in
 * the Selenium framework.</p>
 *
 * <h2>How to run specific test suites</h2>
 * <p>Modify the {@code FILTER_TAGS_PROPERTY_NAME} parameter to control
 * which scenarios execute:</p>
 * <ul>
 *   <li>{@code @regression} — full regression suite</li>
 *   <li>{@code @sanity} — quick smoke suite</li>
 *   <li>{@code @login} — all login tests</li>
 *   <li>{@code @TC001} — single test by ID</li>
 *   <li>{@code not @WIP} — exclude work in progress</li>
 * </ul>
 *
 * <h2>Key difference from Framework 1</h2>
 * <p>JUnit 5 Cucumber uses {@code @Suite} + {@code @ConfigurationParameter}
 * rather than {@code @CucumberOptions}. The configuration parameters map
 * directly to the same concepts — features path, glue path, tags, plugins.</p>
 *
 * <h2>Reports generated</h2>
 * <ul>
 *   <li>Allure — {@code target/allure-results}</li>
 *   <li>Cucumber HTML — {@code target/cucumber-reports/cucumber.html}</li>
 *   <li>Cucumber JSON — {@code target/cucumber-reports/cucumber.json}</li>
 *   <li>Rerun file — {@code target/rerun/failed_scenarios.txt}</li>
 * </ul>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "com.andrewcummins.framework.stepdefs," +
                "com.andrewcummins.framework.hooks"
)
@ConfigurationParameter(
        key = FILTER_TAGS_PROPERTY_NAME,
        value = "@regression"
)
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty," +
                "html:target/cucumber-reports/cucumber.html," +
                "json:target/cucumber-reports/cucumber.json," +
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm," +
                "rerun:target/rerun/failed_scenarios.txt"
)

public class RunCukesTest {

}