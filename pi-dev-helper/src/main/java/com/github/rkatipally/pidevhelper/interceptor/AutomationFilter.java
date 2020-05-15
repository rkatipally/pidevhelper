package com.github.rkatipally.pidevhelper.interceptor;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@Slf4j
public class AutomationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        log.info("Starting Transaction for req :{}", req.getRequestURI());
        HttpServletResponse httpServletResponse = ((HttpServletResponse) response);
        chain.doFilter(request, response);
        log.info("Committing Transaction for req :{}", req.getRequestURI());
    }
}
