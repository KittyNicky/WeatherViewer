package com.kittynicky.app.servlet.auth;

import com.kittynicky.app.exception.ValidationException;
import com.kittynicky.app.mapper.UserMapper;
import com.kittynicky.app.service.SessionService;
import com.kittynicky.app.service.UserService;
import com.kittynicky.app.servlet.MainServlet;
import com.kittynicky.app.util.ViewPath;
import com.kittynicky.app.util.WebServletPath;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@WebServlet(WebServletPath.SIGN_UP)
public class SignUpServlet extends MainServlet {
    private final UserService userService = UserService.getInstance();
    private final UserMapper userMapper = UserMapper.getInstance();
    private final SessionService sessionService = SessionService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        templateEngine.process(ViewPath.SIGN_UP, webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Sign-up process has started");
        try {
            Integer id = userService.create(
                    req.getParameter("login"),
                    req.getParameter("password")
            );
            userService.getById(id)
                    .ifPresent(user -> sessionService.addCookie(userMapper.mapFrom(user), resp));

            log.info("Sign-up process has finished successfully");
            resp.sendRedirect(WebServletPath.HOME);
        } catch (ValidationException | EntityExistsException e) {
            log.warn("Sign-up process has finished unsuccessfully");
            req.setAttribute("errorMessage", e.getMessage());
            doGet(req, resp);
        }
    }
}
