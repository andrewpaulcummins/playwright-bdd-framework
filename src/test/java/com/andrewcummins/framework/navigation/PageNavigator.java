package com.andrewcummins.framework.navigation;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.BasePage;
import com.andrewcummins.framework.poms.pages.InventoryPage;
import com.andrewcummins.framework.poms.pages.LoginPage;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles dynamic page routing based on page name strings from Cucumber feature files.
 *
 * <p>Maps human-readable page name strings to URLs and Page POM instances.
 * Uses reflection-based instantiation to dynamically create Page POM instances
 * by class name — adding a new page requires only a URL entry in
 * {@link #buildUrlMap()} and a class name entry in {@link #buildPageClassMap()}.
 * No switch statement modifications needed.</p>
 *
 * <h2>Reflection-based instantiation</h2>
 * <p>Page POM classes are instantiated dynamically using Java reflection.
 * This eliminates the need to update a switch statement every time a new
 * page is added to the framework — a key architectural improvement over
 * a hardcoded switch approach.</p>
 */
public class PageNavigator {

    private final ScenarioContext context;
    private final Map<String, String> urlMap;
    private final Map<String, Class<? extends BasePage>> pageClassMap;

    /**
     * Constructs a new {@code PageNavigator} and initialises the URL and page class maps.
     *
     * @param context the {@code ScenarioContext} for the current scenario
     */
    public PageNavigator(ScenarioContext context) {
        this.context = context;
        this.urlMap = buildUrlMap();
        this.pageClassMap = buildPageClassMap();
    }

    /**
     * Navigates the browser to the page corresponding to the given page name
     * and returns the matching Page POM instance.
     *
     * @param pageName the page name from the feature file step
     * @return the {@code BasePage} subclass instance for the navigated page
     * @throws RuntimeException if the page name is not registered
     */
    public BasePage navigateTo(String pageName) {
        String normalisedName = normalise(pageName);
        String url = resolveUrl(normalisedName);

        context.getPage().navigate(url);
        context.setCurrentPageName(normalisedName);

        return getPageInstance(normalisedName);
    }

    /**
     * Returns the Page POM instance for the current page without navigating.
     *
     * @return the {@code BasePage} subclass instance for the current page
     * @throws RuntimeException if no current page name has been set
     */
    public BasePage getCurrentPage() {
        String currentPageName = context.getCurrentPageName();

        if (currentPageName == null || currentPageName.trim().isEmpty()) {
            throw new RuntimeException(
                    "[PageNavigator] No current page has been set in ScenarioContext. " +
                            "Ensure a navigation step runs before any step that calls getCurrentPage()."
            );
        }

        return getPageInstance(currentPageName);
    }

    /**
     * Resolves the full URL for the given normalised page name.
     *
     * @param normalisedName the lowercase page name to resolve
     * @return the full URL string
     * @throws RuntimeException if the page name is not found in the URL map
     */
    private String resolveUrl(String normalisedName) {
        String url = urlMap.get(normalisedName);

        if (url == null) {
            throw new RuntimeException(
                    "[PageNavigator] Page '" + normalisedName + "' is not registered. " +
                            "Registered pages are: " + urlMap.keySet() + ". " +
                            "Add the page to buildUrlMap() and buildPageClassMap()."
            );
        }

        return url;
    }

    /**
     * Dynamically instantiates the correct Page POM class for the given page name
     * using Java reflection.
     *
     * <p>This eliminates the need for a switch statement — adding a new page
     * only requires entries in {@link #buildUrlMap()} and {@link #buildPageClassMap()}.</p>
     *
     * @param normalisedName the lowercase page name
     * @return the instantiated {@code BasePage} subclass
     * @throws RuntimeException if no class is registered or instantiation fails
     */
    private BasePage getPageInstance(String normalisedName) {
        Class<? extends BasePage> pageClass = pageClassMap.get(normalisedName);

        if (pageClass == null) {
            throw new RuntimeException(
                    "[PageNavigator] No Page POM registered for page '" +
                            normalisedName + "'. " +
                            "Add an entry to buildPageClassMap() in PageNavigator."
            );
        }

        try {
            Constructor<? extends BasePage> constructor =
                    pageClass.getConstructor(ScenarioContext.class);
            return constructor.newInstance(context);
        } catch (Exception e) {
            throw new RuntimeException(
                    "[PageNavigator] Failed to instantiate Page POM for page '" +
                            normalisedName + "'. " +
                            "Ensure the class has a public constructor accepting ScenarioContext. " +
                            "Error: " + e.getMessage(), e
            );
        }
    }

    /**
     * Builds and returns the map of page name strings to full URLs.
     *
     * @return a populated {@code Map} of page name strings to URLs
     */
    private Map<String, String> buildUrlMap() {
        String baseUrl = context.getConfigReader().getUiBaseUrl();

        Map<String, String> map = new HashMap<>();
        map.put("login",     baseUrl + "/");
        map.put("inventory", baseUrl + "/inventory.html");
        map.put("cart",      baseUrl + "/cart.html");
        map.put("checkout",  baseUrl + "/checkout-step-one.html");
        map.put("overview",  baseUrl + "/checkout-step-two.html");
        map.put("complete",  baseUrl + "/checkout-complete.html");

        return map;
    }

    /**
     * Builds and returns the map of page name strings to Page POM classes.
     *
     * <p>Each entry maps a lowercase page name to its corresponding Page POM class.
     * The class is instantiated dynamically via reflection in {@link #getPageInstance(String)}.</p>
     *
     * @return a populated {@code Map} of page name strings to Page POM classes
     */
    private Map<String, Class<? extends BasePage>> buildPageClassMap() {
        Map<String, Class<? extends BasePage>> map = new HashMap<>();
        map.put("login",     LoginPage.class);
        map.put("inventory", InventoryPage.class);
        map.put("cart",      com.andrewcummins.framework.poms.pages.CartPage.class);
        map.put("checkout",  com.andrewcummins.framework.poms.pages.CheckoutPage.class);
        map.put("overview",  com.andrewcummins.framework.poms.pages.OverviewPage.class);
        map.put("complete",  com.andrewcummins.framework.poms.pages.CompletePage.class);

        return map;
    }

    /**
     * Normalises a page name string to lowercase and trims whitespace.
     *
     * @param pageName the raw page name string
     * @return the normalised lowercase page name
     */
    private String normalise(String pageName) {
        return pageName.toLowerCase().trim();
    }
}