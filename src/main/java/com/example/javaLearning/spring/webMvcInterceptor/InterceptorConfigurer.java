package com.example.javaLearning.spring.webMvcInterceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@Slf4j
public class InterceptorConfigurer implements WebMvcConfigurer {

  @Autowired
  private final Interceptor interceptor;

  public InterceptorConfigurer(Interceptor interceptor) {
    this.interceptor = interceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    log.info("InterceptorConfigurer: Request came to the web mvc interceptor, adding custom "
        + "interceptor");
    registry.addInterceptor(interceptor);
  }
}
