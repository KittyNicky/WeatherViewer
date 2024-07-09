package com.kittynicky.app.servlet.auth;

import com.kittynicky.app.entity.Session;
import com.kittynicky.app.service.SessionService;
import com.kittynicky.app.servlet.MainServlet;
import com.kittynicky.app.util.Utils;
import com.kittynicky.app.util.WebServletPath;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@WebServlet(WebServletPath.SIGN_OUT)
public class SignOutServlet extends MainServlet {
    private final SessionService sessionService = SessionService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Sign-out process has started");
        Optional<Cookie> cookie = Utils.getCookie(req.getCookies(), SessionService.COOKIE_SESSION_ATTR);
        cookie.ifPresent(c -> {
            Optional<Session> session = sessionService.getSession(UUID.fromString(c.getValue()));
            sessionService.deleteCookie(resp);
            session.ifPresent(sessionService::delete);
        });
        log.info("Sign-out process has finished successfully");
        resp.sendRedirect(WebServletPath.HOME);
    }
}
