/**
 * Jenkins Pipeline for Selenium BDD Framework
 *
 * This pipeline automates the build, test, and reporting lifecycle
 * for the Selenium BDD test automation framework. It is triggered
 * automatically on every push to the repository and can also be
 * run manually from the Jenkins UI.
 *
 * PIPELINE STAGES:
 * 1. Checkout     — clones the repository from source control
 * 2. Build        — compiles the framework and resolves dependencies
 * 3. Test         — executes the Cucumber test suite via Maven
 * 4. Reports      — generates and publishes the Allure report
 * 5. Notify       — outputs final pipeline status to console
 *
 * PREREQUISITES:
 * The following must be configured in Jenkins before running:
 *   - JDK 17 configured as a tool named "JDK17"
 *   - Maven 3.9.x configured as a tool named "Maven3"
 *   - Allure configured as a tool named "Allure"
 *   - Secret text credential named "TEST_DATA_SECRET_KEY"
 *     containing the AES encryption key for test data decryption
 *
 * TAGS:
 * The test suite tag can be overridden at runtime using the
 * CUCUMBER_TAGS parameter. Default is "@regression or @sanity".
 */
pipeline {

    // Run on any available Jenkins agent.
    // Replace 'any' with a specific agent label if your Jenkins
    // infrastructure uses labelled agents (e.g. agent { label 'linux' })
    agent any

    // =========================================================================
    // TOOLS
    // =========================================================================
    // References tools configured in Jenkins Global Tool Configuration.
    // Ensures the correct JDK and Maven versions are on the PATH for
    // every stage without relying on whatever is installed on the agent.
    // =========================================================================
    tools {
        jdk 'JDK17'
        maven 'Maven3'
    }

    // =========================================================================
    // PARAMETERS
    // =========================================================================
    // Parameters allow the pipeline to be customised at runtime from the
    // Jenkins UI without modifying the Jenkinsfile itself.
    // Click "Build with Parameters" in Jenkins to override these values.
    // =========================================================================
    parameters {
        string(
            name: 'CUCUMBER_TAGS',
            defaultValue: '@regression or @sanity',
            description: 'Cucumber tag expression controlling which scenarios run. ' +
                         'Examples: @regression, @sanity, @login, @TC001, not @WIP'
        )
        choice(
            name: 'BROWSER',
            choices: ['chrome', 'firefox', 'edge'],
            description: 'Browser to run the test suite against.'
        )
        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run browser in headless mode. ' +
                         'Always true in CI — set to false for local debugging only.'
        )
    }

    // =========================================================================
    // ENVIRONMENT
    // =========================================================================
    // Environment variables available to all stages in the pipeline.
    //
    // TEST_DATA_SECRET_KEY is loaded from Jenkins Credentials Store — the key
    // is never stored in plain text in the Jenkinsfile or the repository.
    // Add it in Jenkins: Manage Jenkins → Credentials → Add Credentials
    // → Kind: Secret text → ID: TEST_DATA_SECRET_KEY
    // =========================================================================
    environment {
        TEST_DATA_SECRET_KEY = credentials('TEST_DATA_SECRET_KEY')
        MAVEN_OPTS = '-Xmx1024m'
    }

    // =========================================================================
    // STAGES
    // =========================================================================
    stages {

        // =====================================================================
        // STAGE 1: CHECKOUT
        // Clones the repository from source control. In a typical Jenkins
        // setup with a Multibranch Pipeline or GitHub integration, this
        // happens automatically. This stage makes it explicit and provides
        // a clear checkpoint in the pipeline UI.
        // =====================================================================
        stage('Checkout') {
            steps {
                echo "Checking out branch: ${env.BRANCH_NAME ?: 'main'}"
                checkout scm
            }
        }

        // =====================================================================
        // STAGE 2: BUILD
        // Compiles all Java source files and downloads dependencies.
        // Uses 'test-compile' rather than 'compile' to ensure test
        // classes are compiled and ready for the test stage.
        // The -T flag enables parallel module building where applicable.
        // =====================================================================
        stage('Build') {
            steps {
                echo 'Compiling framework and resolving dependencies...'
                sh 'mvn test-compile -T 1C'
            }
        }

        stage('Install Playwright Browsers') {
            steps {
                echo 'Installing Playwright browser binaries...'
                sh 'mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"'
            }
        }

        // =====================================================================
        // STAGE 3: TEST
        // Executes the Cucumber test suite via Maven Surefire.
        // Key Maven properties passed at runtime:
        //   -Dcucumber.filter.tags  : overrides the tags in RunCukesTest.java
        //   -Dbrowser               : overrides browser in config.properties
        //   -Dheadless              : overrides headless in config.properties
        //
        // '|| true' at the end prevents the pipeline from marking the entire
        // build as FAILED when tests fail — test failures are reported via
        // the Allure report rather than as a pipeline failure. Remove this
        // if you want the pipeline to fail on test failures.
        // =====================================================================
        stage('Test') {
            steps {
                echo "Running test suite with tags: ${params.CUCUMBER_TAGS}"
                echo "Browser: ${params.BROWSER} | Headless: ${params.HEADLESS}"

                sh """
                    mvn test \
                        -Dcucumber.filter.tags="${params.CUCUMBER_TAGS}" \
                        -Dbrowser=${params.BROWSER} \
                        -Dheadless=${params.HEADLESS} \
                        -Dallure.results.directory=target/allure-results
                """
            }
            post {
                always {
                    // Archive the Cucumber JSON report for downstream processing
                    archiveArtifacts artifacts: 'target/cucumber-reports/**/*',
                                     allowEmptyArchive: true

                    // Archive failed scenario rerun file
                    archiveArtifacts artifacts: 'target/rerun/**/*',
                                     allowEmptyArchive: true

                    // Publish Cucumber HTML report in Jenkins UI
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/cucumber-reports',
                        reportFiles: 'cucumber.html',
                        reportName: 'Cucumber Report',
                        reportTitles: 'Cucumber Test Results'
                    ])
                }
            }
        }

        // =====================================================================
        // STAGE 4: REPORTS
        // Generates and publishes the Allure report from the raw JSON
        // results captured during the test stage.
        //
        // The allure.results.directory property tells the Allure plugin
        // where to find the raw result files generated during test execution.
        //
        // The published report is accessible from the Jenkins build page
        // via the "Allure Report" link in the left sidebar.
        // =====================================================================
        stage('Reports') {
            steps {
                echo 'Generating Allure report...'
                allure([
                    includeProperties: false,
                    jdk: 'JDK17',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'target/allure-results']]
                ])
            }
        }
    }

    // =========================================================================
    // POST
    // =========================================================================
    // Actions that run after all stages complete, regardless of outcome.
    // =========================================================================
    post {

        always {
            echo 'Pipeline complete. Cleaning workspace...'
            cleanWs()
        }

        success {
            echo '✅ Pipeline PASSED — all stages completed successfully.'
        }

        failure {
            echo '❌ Pipeline FAILED — check the Allure report for details.'
        }

        unstable {
            echo '⚠️ Pipeline UNSTABLE — some tests may have failed.'
        }
    }
}