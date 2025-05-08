package com.cs_test.turvaauk.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class PreflightOnlyCorsFilter implements Filter {
    private static final Set<String> ALLOWED = Set.of("http://turvaauk.localtest.me");

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String origin = request.getHeader("Origin");

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            if (origin != null && ALLOWED.contains(origin)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
                response.setHeader("Access-Control-Allow-Headers", "Content-Type");
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
            return;
        }

        chain.doFilter(req, res);
    }
}