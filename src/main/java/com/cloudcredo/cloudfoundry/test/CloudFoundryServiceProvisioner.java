package com.cloudcredo.cloudfoundry.test;

import com.cloudcredo.cloudfoundry.test.annotation.RabbitMQCloudFoundryService;
import com.cloudcredo.cloudfoundry.test.annotation.RedisCloudFoundryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple Object that delegates to an an instance of Cloud Foundry, creates the required service and set the environment
 * variable for the Java process.
 *
 * @author: chris
 * @date: 29/04/2013
 */
public class CloudFoundryServiceProvisioner {

    private static final Logger log = LoggerFactory.getLogger(CloudFoundryServiceProvisioner.class);

    /** Sets the Environment variables that the Spring Cloud Foundry module expect */
    private CloudFoundryEnvironmentAdapter cloudFoundryEnvironmentAdapter = new CloudFoundryEnvironmentAdapter();

    /** Creates the required service in Cloud Foundry and returns the generated Credentials */
    private NatsCloudFoundryServicesClient natsCloudFoundryServicesClient = new NatsCloudFoundryServicesClient();

    /**
     * Creates a RabbitMQ service in the target instance of Cloud Foundry and sets the VCAP_SERVICES environment
     * variables as required by Spring for auto-connect functionality.
     */
    private Credentials createRabbitMqService() {
        try {
            log.info("Creating new RabbitMQ Cloud Foundry Service");
            return natsCloudFoundryServicesClient.getCredentialsForNewService("rabbit-test", CloudFoundryService.RABBITMQ);
        } catch (InterruptedException e) {
            throw new RuntimeException("Cannot Create RabbitMQ Service");
        }
    }

    /**
     * Creates a Redis service in the target instance of Cloud Foundry and sets the VCAP_SERVICES environment variables
     * as required by Spring for auto-connect functionality.
     */
    private Credentials createRedisService() {
        try {
            log.info("Creating new Redis Cloud Foundry Service");
            return natsCloudFoundryServicesClient.getCredentialsForNewService("redis-test", CloudFoundryService.REDIS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Cannot Create Redis Service");
        }
    }

    /**
     * Looks for the Service annotations on the test class and creates a service for each found mixin interface.
     *
     * @param clazz Class to look for presence of annotations on.
     * @see com.cloudcredo.cloudfoundry.test.annotation
     */
    void createServicesForClass(Class clazz) {
        Map<CloudFoundryService, Credentials> credentials = new HashMap<CloudFoundryService, Credentials>();
        if (clazz.isAnnotationPresent(RabbitMQCloudFoundryService.class)) {
            credentials.put(CloudFoundryService.RABBITMQ, createRabbitMqService());
        }

        if (clazz.isAnnotationPresent(RedisCloudFoundryService.class)) {
            credentials.put(CloudFoundryService.REDIS, createRedisService());
        }

        cloudFoundryEnvironmentAdapter.addVcapServices(credentials);
    }
}
