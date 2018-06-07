package ru.kpfu.itis.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class SubscriptionCheckFilter implements Filter {

    private final static Logger LOGGER = LoggerFactory.getLogger(SubscriptionCheckFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String resourcePath = new UrlPathHelper().getPathWithinApplication(httpServletRequest);
        if (isCurrentUserHasUnlimitedAccess(httpServletRequest)) {
            return;
        } else if (checkIsPathFree(resourcePath)) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            checkExpiredAndRedirect(httpServletResponse);
        }
        chain.doFilter(request, response);
    }

    protected abstract boolean isCurrentUserHasUnlimitedAccess(HttpServletRequest request);

    protected abstract void checkExpiredAndRedirect(HttpServletResponse httpServletResponse);

    protected abstract boolean checkIsPathFree(String resourcePath);

    @Override
    public void destroy() {

    }
}
