package BsmchFlights;

import BsmchFlights.Interceptor.JWTUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTUserInterceptor()).addPathPatterns("/users");
//        registry.addInterceptor(new JWTUserInterceptor()).addPathPatterns("/");
    }
}
