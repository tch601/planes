package BsmchFlights.Interceptor;

import BsmchFlights.Models.MyJWT;
import BsmchFlights.Models.User;
import BsmchFlights.Services.FlightsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JWTUserInterceptor implements HandlerInterceptor {
    private final Logger logger = LoggerFactory.getLogger(FlightsService.class);
    private final int BASE_PERMISSION_LEVEL = 2;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("JWTUserInterceptor : preHandle");
        String authorization = request.getHeader("Authorization");
        User user = MyJWT.decodeUser(authorization);

        return user.getPermissions().getId() <= BASE_PERMISSION_LEVEL;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.info("JWTUserInterceptor : postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.info("JWTUserInterceptor : afterCompletion");
    }
}
