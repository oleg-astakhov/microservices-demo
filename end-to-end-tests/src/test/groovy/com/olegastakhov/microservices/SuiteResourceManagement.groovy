package com.olegastakhov.microservices

import org.spockframework.runtime.extension.IGlobalExtension
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.wait.strategy.Wait

import java.time.Duration

/**
 * For this code to work, a file with name:
 * org.spockframework.runtime.extension.IGlobalExtension
 * must exist under directory META-INF/services and be
 * found in the CLASSPATH. The contents of the file
 * must be the fully qualified name of this class
 */

class SuiteResourceManagement implements IGlobalExtension {
    DockerComposeContainer compose

    SuiteResourceManagement() {
        if (Environment.getInstance().isCiMode()) {
            compose = new DockerComposeContainer(
                    new File("../compose-common.yml"),
                    new File("../compose-e2e-test.yml")
            ).withLocalCompose(true)
                    .waitingFor("frontend", Wait.forHttp("/")
                            .withStartupTimeout(Duration.ofSeconds(45))
                    )
        } else {
            println "${SuiteResourceManagement.class.getSimpleName()} Expecting DOCKER COMPOSE to be launched externally..."
        }
    }


    void start() {
        if (null != compose) {
            println "${SuiteResourceManagement.class.getSimpleName()}  STARTING DOCKER COMPOSE..."
            compose.start()
        }
    };


    void stop() {
        if (null != compose) {
            println "${SuiteResourceManagement.class.getSimpleName()}  STOPPING DOCKER COMPOSE..."
            compose.stop();
        }
    };
}
