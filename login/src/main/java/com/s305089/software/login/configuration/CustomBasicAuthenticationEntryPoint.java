package com.s305089.software.login.configuration;

import com.s305089.software.login.logging.APIMessage;
import com.s305089.software.login.logging.HistoryConnector;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@Component
public class CustomBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    //http://www.baeldung.com/spring-security-basic-authentication
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        super.commence(request, response, authException);
        response.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        writer.println("HTTP Status 401 - " + authException.getMessage());

        if (authException instanceof InsufficientAuthenticationException) {
            new Thread(() -> {
                APIMessage apiMessage = new APIMessage("CustomBasicAuthentication", false, request.getServletPath());
                HistoryConnector.logToLogService(apiMessage, "/user");
            }).start();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("Svergja");
        super.afterPropertiesSet();
    }
}
