package com.kittynicky.app.filter;

import com.kittynicky.app.service.SessionService;
import com.kittynicky.app.util.Utils;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.kittynicky.app.service.SessionService.COOKIE_SESSION_ATTR;

@WebFilter("/*")
public class SessionFilter implements Filter {
    private final SessionService sessionService = SessionService.getInstance();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        sessionService.removeAllExpiredSessions();

        Optional<Cookie> cookieSession = Utils.getCookie(req.getCookies(), COOKIE_SESSION_ATTR);
        cookieSession.flatMap(cookie -> sessionService.getSession(UUID.fromString(cookie.getValue())))
                .ifPresentOrElse(
                        session -> {
                            req.setAttribute("login", session.getUser().getLogin());
                            sessionService.updateExpiredAt(session);
                        },
                        () -> sessionService.deleteCookie(resp)
                );

        chain.doFilter(request, response);
    }
}
