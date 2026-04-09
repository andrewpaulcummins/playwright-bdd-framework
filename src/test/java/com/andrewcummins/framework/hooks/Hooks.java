package com.andrewcummins.framework.hooks;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.factory.BrowserFactory;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.nio.file.Paths;

/**
 * Cucumber lifecycle hooks for the Playwright BDD framework.
 *
 * <p>Responsible for creating and tearing down the full Playwright object
 * hierarchy per scenario — Playwright → Browser → BrowserContext → Page.
 * Screenshots and traces are captured on failure and attached to the report.</p>
 *
 * <h2>Playwright teardown order</h2>
 * <p>Objects must be closed in reverse creation order:</p>
 * <ol>
 *   <li>Stop tracing on {@code BrowserContext}</li>
 *   <li>Close {@code BrowserContext}</li>
 *   <li>Close {@code Browser}</li>
 *   <li>Close {@code Playwright}</li>
 * </ol>
 *
 * <h2>Key advantage over Selenium</h2>
 * <p>A fresh {@code BrowserContext} per scenario provides complete isolation
 * without restarting the browser — cookies, localStorage, and session state
 * are never shared between scenarios, even in parallel execution.</p>
 */
public class Hooks {

    private final ScenarioContext context;
    private final BrowserFactory browserFactory;

    /**
     * Constructs a new {@code Hooks} instance with the injected {@code ScenarioContext}.
     * PicoContainer injects the shared context automatically.
     *
     * @param context the {@code ScenarioContext} for the current scenario
     */
    public Hooks(ScenarioContext context) {
        this.context = context;
        this.browserFactory = new BrowserFactory(context.getConfigReader());
    }

    /**
     * Sets up the full Playwright object hierarchy before each scenario.
     *
     * <p>Creates Playwright → Browser → BrowserContext → Page in order,
     * sets each on the ScenarioContext, and starts tracing if configured.</p>
     *
     * @param scenario the current Cucumber {@code Scenario}
     */
    @Before(order = 1)
    public void setUp(Scenario scenario) {
        System.out.println("[Hooks] Starting scenario: " + scenario.getName());
        System.out.println("[Hooks] Tags: " + scenario.getSourceTagNames());

        Playwright playwright = browserFactory.createPlaywright();
        Browser browser = browserFactory.createBrowser(playwright);
        BrowserContext browserContext = browserFactory.createBrowserContext(browser);
        Page page = browserFactory.createPage(browserContext);

        context.setPlaywright(playwright);
        context.setBrowser(browser);
        context.setBrowserContext(browserContext);
        context.setPage(page);

        browserFactory.startTracing(browserContext);

        System.out.println("[Hooks] Browser initialised: " +
                context.getConfigReader().getBrowser().toUpperCase());
    }

    /**
     * Tears down the Playwright object hierarchy after each scenario.
     *
     * <p>Captures screenshot and trace on failure, then closes all Playwright
     * objects in reverse creation order. Always runs regardless of outcome.</p>
     *
     * @param scenario the current Cucumber {@code Scenario}
     */
    @After(order = 1)
    public void tearDown(Scenario scenario) {
        System.out.println("[Hooks] Scenario '" + scenario.getName() +
                "' finished with status: " + scenario.getStatus());

        boolean scenarioFailed = scenario.isFailed();

        if (scenarioFailed) {
            captureScreenshot(scenario);
        }

        stopTracing(scenarioFailed, scenario.getName());
        closePlaywright();
    }

    /**
     * Captures a screenshot after each failed step.
     *
     * @param scenario the current Cucumber {@code Scenario}
     */
    @AfterStep
    public void captureStepScreenshot(Scenario scenario) {
        if (scenario.isFailed()) {
            captureScreenshot(scenario);
        }
    }

    /**
     * Captures a screenshot of the current page and attaches it to the report.
     *
     * @param scenario the Cucumber {@code Scenario} to attach the screenshot to
     */
    private void captureScreenshot(Scenario scenario) {
        try {
            byte[] screenshot = context.getPage().screenshot();
            scenario.attach(screenshot, "image/png", "Screenshot on failure");
            System.out.println("[Hooks] Screenshot captured and attached to report.");
        } catch (Exception e) {
            System.err.println("[Hooks] Failed to capture screenshot: " + e.getMessage());
        }
    }

    /**
     * Stops the Playwright trace recorder and saves the trace file if appropriate.
     *
     * @param scenarioFailed {@code true} if the scenario failed
     * @param scenarioName   the scenario name used for the trace file name
     */
    private void stopTracing(boolean scenarioFailed, String scenarioName) {
        try {
            String tracePath = context.getConfigReader().getTracePath() + "/" +
                    scenarioName.replaceAll("[^a-zA-Z0-9]", "_") + ".zip";

            browserFactory.stopTracing(
                    context.getBrowserContext(),
                    scenarioFailed,
                    tracePath
            );
        } catch (Exception e) {
            System.err.println("[Hooks] Failed to stop tracing: " + e.getMessage());
        }
    }

    /**
     * Closes all Playwright objects in reverse creation order.
     *
     * <p>Page is closed automatically when BrowserContext is closed.
     * Each close is wrapped in a null-safe try/catch to prevent secondary
     * exceptions masking the original test failure.</p>
     */
    private void closePlaywright() {
        try {
            if (context.getBrowserContext() != null) {
                context.getBrowserContext().close();
                System.out.println("[Hooks] BrowserContext closed.");
            }
        } catch (Exception e) {
            System.err.println("[Hooks] Failed to close BrowserContext: " + e.getMessage());
        }

        try {
            if (context.getBrowser() != null) {
                context.getBrowser().close();
                System.out.println("[Hooks] Browser closed.");
            }
        } catch (Exception e) {
            System.err.println("[Hooks] Failed to close Browser: " + e.getMessage());
        }

        try {
            if (context.getPlaywright() != null) {
                context.getPlaywright().close();
                System.out.println("[Hooks] Playwright closed.");
            }
        } catch (Exception e) {
            System.err.println("[Hooks] Failed to close Playwright: " + e.getMessage());
        }
    }
}