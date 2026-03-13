package com.auth.letter.config;

import com.auth.letter.controller.*;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.util.stream.Collectors;

/**
 * Jersey Configuration
 */
@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        // Register all controllers with @Path annotation
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Path.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Provider.class));

        registerClasses(scanner.findCandidateComponents("com.auth.letter.controller").stream()
                .map(beanDefinition -> ClassUtils.resolveClassName(beanDefinition.getBeanClassName(), getClassLoader()))
                .collect(Collectors.toSet()));

        // Register JSON provider
        packages("com.fasterxml.jackson.jaxrs.json");
    }
}