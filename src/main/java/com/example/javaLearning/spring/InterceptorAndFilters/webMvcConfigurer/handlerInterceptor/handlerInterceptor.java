package com.example.javaLearning.spring.InterceptorAndFilters.webMvcConfigurer.handlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Slf4j
@Component
public class handlerInterceptor implements HandlerInterceptor {

  public static final String USER_ID = "USER-ID";
  public static final String LOG_TRACKING_ID = "LOG-TRACKING-ID";

  // As handlerInterceptor has default methods need not override all the methods in the interface

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    log.info(
        "handlerInterceptor: Request came to interceptor for appending userId and trackingId in the log");
    String trackingId = request.getHeader(LOG_TRACKING_ID);
    String userId = request.getHeader(USER_ID);

    if (null == trackingId) {
      MDC.put(LOG_TRACKING_ID, UUID.randomUUID().toString());
    }
    if (null == userId) {
      MDC.put(USER_ID, "1L");
    }
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, @Nullable Exception ex) throws Exception {
    log.info("handlerInterceptor: Removing values from MDC");
    MDC.remove(LOG_TRACKING_ID);
    MDC.remove(USER_ID);
  }
}
