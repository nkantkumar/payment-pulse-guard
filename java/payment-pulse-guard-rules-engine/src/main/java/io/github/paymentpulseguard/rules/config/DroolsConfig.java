package io.github.paymentpulseguard.rules.config;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

@Configuration
public class DroolsConfig {

    private static final String RULES_PATH = "io/github/paymentpulseguard/rules/";

    @Bean
    public KieContainer kieContainer() throws IOException {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        // Explicitly find and add all .drl files
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        // MODIFIED LINE: Only look for .drl files
        Resource[] files = resourcePatternResolver.getResources("classpath*:" + RULES_PATH + "**/*.drl");
        for (Resource file : files) {
            // The path for KieFileSystem.write needs to be relative to the KIE base,
            // which is effectively the root of the classpath for the DRL files.
            // Using file.getURI().toString() or file.getFilename() might not give the full path expected by KieFileSystem.write
            // Let's use the full path relative to resources, which is what ResourceFactory.newClassPathResource expects.
            // The original ResourceFactory.newClassPathResource("rules/rules.drl") was simpler and worked when the path was fixed.
            // Let's revert to a simpler write that assumes the file is directly in the path.
            // Or, more robustly, ensure the path is correct for the KieFileSystem.
            // The `file.getFilename()` is just "rules.drl". We need the full path.
            // The `file.getURL().getPath()` might give an absolute path, which is not what KieFileSystem expects.
            // The `ResourceFactory.newClassPathResource` expects a path relative to the classpath root.
            // So, if RULES_PATH is "io/github/paymentpulseguard/rules/", and file.getFilename() is "rules.drl",
            // then RULES_PATH + file.getFilename() is "io/github/paymentpulseguard/rules/rules.drl", which is correct.
            kieFileSystem.write(ResourceFactory.newClassPathResource(RULES_PATH + file.getFilename(), "UTF-8"));
        }

        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();

        if (kieBuilder.getResults().hasMessages(Message.Level.ERROR)) {
            throw new RuntimeException("Build Errors: " + kieBuilder.getResults().toString());
        }

        return kieServices.getKieClasspathContainer();
    }
}
