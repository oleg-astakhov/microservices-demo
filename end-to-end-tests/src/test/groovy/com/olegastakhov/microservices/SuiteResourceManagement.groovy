package com.olegastakhov.microservices

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.spockframework.runtime.extension.IGlobalExtension
import org.testcontainers.containers.ComposeContainer

/**
 * For this code to work, a file with name:
 * org.spockframework.runtime.extension.IGlobalExtension
 * must exist under directory META-INF/services and be
 * found in the CLASSPATH. The contents of the file
 * must be the fully qualified name of this class
 */

class SuiteResourceManagement implements IGlobalExtension {
    private static final Logger log = LoggerFactory.getLogger(SuiteResourceManagement.class);

    ComposeContainer compose

    SuiteResourceManagement() {
        if (Environment.getInstance().isCiMode()) {
            compose = new ComposeContainer(
                    new File("../compose-common.yml"),
                    new File("../compose-e2e-test.yml")
            ).withLocalCompose(true)
            .withBuild(true)
        } else {
            log.info("${SuiteResourceManagement.class.getSimpleName()} Expecting DOCKER COMPOSE to be launched externally...")
        }
    }


    void start() {
        if (null != compose) {
            log.info("${SuiteResourceManagement.class.getSimpleName()}  STARTING DOCKER COMPOSE...")
            compose.start()
        }
    };


    void stop() {
        if (null != compose) {
            log.info("${SuiteResourceManagement.class.getSimpleName()}  STOPPING DOCKER COMPOSE...")
            compose.stop();
        }
    };
}
