package com.example.javaLearning.spring.InterceptorAndFilters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@Slf4j
public class InterceptorConfigurer implements WebMvcConfigurer {

  @Autowired
  private final handlerInterceptor handlerInterceptor;

  public InterceptorConfigurer(handlerInterceptor handlerInterceptor) {
    this.handlerInterceptor = handlerInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    log.info("InterceptorConfigurer: Request came to the web mvc handlerInterceptor, adding custom "
        + "handlerInterceptor");
    registry.addInterceptor(handlerInterceptor);
  }
}
