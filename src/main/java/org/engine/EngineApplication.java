package org.engine;

import net.kaczmarzyk.spring.data.jpa.web.SpecificationArgumentResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@EnableSwagger2
@SpringBootApplication(scanBasePackages = { "org.engine.*", "org.engine.production.service", "org.engine.warehouse.service" })
public class EngineApplication implements WebMvcConfigurer {

    /**
     * Enable https://github.com/tkaczmarzyk/specification-arg-resolver
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new SpecificationArgumentResolver());
    }

    public static void main(final String[] args)
    {
        SpringApplication.run(EngineApplication.class, args);
    }
}
