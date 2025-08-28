package com.casestudy.credit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

@Slf4j
public class CleanupH2DatabaseTestListener implements TestExecutionListener, Ordered {

    private static final String H2_SCHEMA_NAME = "PUBLIC";

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        TestExecutionListener.super.beforeTestMethod(testContext);
        cleanupDatabase(testContext);
    }

    private void cleanupDatabase(TestContext testContext) {
        log.info("Cleaning up database begin");
        CleanupH2DbService cleanupH2DbService = testContext.getApplicationContext().getBean(CleanupH2DbService.class);
        cleanupH2DbService.cleanup(H2_SCHEMA_NAME);
        log.info("Cleaning up database end");
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
