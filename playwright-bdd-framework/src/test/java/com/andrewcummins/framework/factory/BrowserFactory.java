package com.andrewcummins.framework.factory;

import com.andrewcummins.framework.utils.ConfigReader;
import com.microsoft.playwright.*;

import java.util.Arrays;

/**
 * Factory class responsible for creating and configuring Playwright browser instances.
 *
 * <p>Implements the Factory design pattern for Playwright browser creation.
 * All browser-specific configuration — browser type, headless mode, slow motion,
 * timeouts, and viewport — is centralised here.</p>
 *
 * <h2>Playwright object hierarchy created here</h2>
 * <ol>
 *   <li>{@code Playwright} — root instance</li>
 *   <li>{@code Browser} — launched browser</li>
 *   <li>{@code BrowserContext} — isolated session</li>
 *   <li>{@code Page} — active tab</li>
 * </ol>
 *
 * <h2>Key advantage over Selenium</h2>
 * <p>Playwright manages its own browser binaries — no WebDriverManager,
 * no ChromeDriver version matching, no PATH configuration required.
 * Run {@code mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI
 * -D exec.args="install"} once to download all browser binaries.</p>
 *
 * <h2>Supported browsers</h2>
 * <ul>
 *   <li>{@code chromium} — Chromium (default)</li>
 *   <li>{@code firefox} — Mozilla Firefox</li>
 *   <li>{@code webkit} — Apple WebKit (Safari engine)</li>
 * </ul>
 */
public class BrowserFactory {

    private final ConfigReader configReader;

    /**
     * Constructs a new {@code BrowserFactory} with the given {@code ConfigReader}.
     *
     * @param configReader the configuration reader providing browser settings
     */
    public BrowserFactory(ConfigReader configReader) {
        this.configReader = configReader;
    }

    /**
     * Creates and returns a configured Playwright {@code Playwright} root instance.
     *
     * <p>The Playwright instance must be created before any browser can be launched.
     * It manages the lifecycle of browser processes and should be closed in the
     * {@code @After} hook after the browser and context have been closed.</p>
     *
     * @return a new {@code Playwright} instance
     */
    public Playwright createPlaywright() {
        return Playwright.create();
    }

    /**
     * Launches and returns a configured {@code Browser} instance.
     *
     * <p>The browser type is determined by the {@code browser} property in
     * {@code config.properties}. Headless mode and slow motion are also
     * applied from configuration.</p>
     *
     * @param playwright the Playwright root instance to launch the browser from
     * @return a launched and configured {@code Browser} instance
     * @throws RuntimeException if the configured browser type is not supported
     */
    public Browser createBrowser(Playwright playwright) {
        String browserType = configReader.getBrowser();
        boolean headless = configReader.isHeadless();
        int slowMotion = configReader.getSlowMotion();

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(slowMotion);

        switch (browserType) {
            case "chromium":
                return playwright.chromium().launch(launchOptions);
            case "firefox":
                return playwright.firefox().launch(launchOptions);
            case "webkit":
                return playwright.webkit().launch(launchOptions);
            default:
                throw new RuntimeException(
                        "[BrowserFactory] Unsupported browser: '" + browserType + "'. " +
                                "Supported values are: 'chromium', 'firefox', 'webkit'. " +
                                "Check the 'browser' property in config.properties."
                );
        }
    }

    /**
     * Creates and returns a configured {@code BrowserContext} from the given browser.
     *
     * <p>Each scenario gets a fresh {@code BrowserContext} — equivalent to a new
     * incognito window. This ensures complete isolation between scenarios:
     * cookies, localStorage, and authentication state are never shared.</p>
     *
     * <p>This is a key architectural advantage of Playwright over Selenium —
     * context-level isolation is built in without requiring browser restarts.</p>
     *
     * @param browser the launched {@code Browser} instance
     * @return a new isolated {@code BrowserContext}
     */
    public BrowserContext createBrowserContext(Browser browser) {
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
                .setViewportSize(1920, 1080)
                .setIgnoreHTTPSErrors(true);

        return browser.newContext(contextOptions);
    }

    /**
     * Creates and returns a new {@code Page} from the given {@code BrowserContext}.
     *
     * <p>The Page is the primary object used for all browser interactions in
     * page objects. Default and navigation timeouts are applied here from
     * configuration.</p>
     *
     * @param browserContext the {@code BrowserContext} to create the page from
     * @return a configured {@code Page} instance
     */
    public Page createPage(BrowserContext browserContext) {
        Page page = browserContext.newPage();

        page.setDefaultTimeout(configReader.getDefaultTimeout());
        page.setDefaultNavigationTimeout(configReader.getNavigationTimeout());

        return page;
    }

    /**
     * Configures the Playwright trace recorder on the given {@code BrowserContext}.
     *
     * <p>The trace captures screenshots, DOM snapshots, network requests, and
     * console logs throughout the scenario. On failure, the trace file can be
     * opened with {@code playwright show-trace trace.zip} for full debugging.</p>
     *
     * <p>Trace mode is controlled by the {@code trace.mode} property:</p>
     * <ul>
     *   <li>{@code on} — always record traces</li>
     *   <li>{@code off} — never record traces</li>
     *   <li>{@code retain-on-failure} — only keep traces for failed scenarios</li>
     * </ul>
     *
     * @param browserContext the {@code BrowserContext} to start tracing on
     */
    public void startTracing(BrowserContext browserContext) {
        String traceMode = configReader.getTraceMode();

        if (!traceMode.equals("off")) {
            browserContext.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(false)
            );
        }
    }

    /**
     * Stops the trace recorder and saves the trace file if appropriate.
     *
     * <p>When trace mode is {@code retain-on-failure}, the trace is only
     * saved if the scenario failed. When trace mode is {@code on}, the
     * trace is always saved regardless of outcome.</p>
     *
     * @param browserContext the {@code BrowserContext} to stop tracing on
     * @param scenarioFailed {@code true} if the scenario failed
     * @param tracePath      the file path to save the trace zip file to
     */
    public void stopTracing(BrowserContext browserContext,
                            boolean scenarioFailed,
                            String tracePath) {
        String traceMode = configReader.getTraceMode();

        if (traceMode.equals("off")) {
            return;
        }

        boolean shouldSave = traceMode.equals("on") ||
                (traceMode.equals("retain-on-failure") && scenarioFailed);

        if (shouldSave) {
            browserContext.tracing().stop(
                    new Tracing.StopOptions().setPath(java.nio.file.Paths.get(tracePath))
            );
        } else {
            browserContext.tracing().stop();
        }
    }
}