package com.drools;

import java.io.IOException;

import javax.inject.Inject;

import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@SpringBootApplication
@PropertySource("classpath:/com/drools/configs/drools-app.properties")
public class DroolsApplication {
    
    @Inject
    private Environment env;
    
    public static void main(String[] args) {
        SpringApplication.run(DroolsApplication.class, args);
    }

    @Bean
    public KieContainer kieContainer() throws IOException {
        final String url = env.getProperty("workbench.url") + 
                        env.getProperty("workbench.repo.path") + 
                        env.getProperty("workbench.repo.artifact.path") + 
                        env.getProperty("workbench.repo.artifact.name");
        final KieServices kieService = KieServices.Factory.get();
//        final KieRepository kieRepo = kieService.getRepository();
//        final UrlResource urlResource = (UrlResource) kieService.getResources().newUrlResource(url);
//        urlResource.setUsername(env.getProperty("workbench.username"));
//        urlResource.setPassword(env.getProperty("workbench.password"));
//        urlResource.setBasicAuthentication("enabled");
//        final InputStream is = urlResource.getInputStream();
//        final KieModule kieModule = kieRepo.addKieModule(kieService.getResources().newInputStreamResource(is));
//        KieContainer kContainer = kieService.newKieContainer(kieModule.getReleaseId()); 
        ReleaseId releaseId = kieService.newReleaseId("com.drools.facts", "drools-demo-facts-KJar", "1.0.0-SNAPSHOT");  
        KieContainer kContainer = kieService.newKieContainer(releaseId);  
        
        KieScanner kScanner = kieService.newKieScanner(kContainer); 
        kScanner.start(1000L);  
        return kContainer;
       
    }
}
