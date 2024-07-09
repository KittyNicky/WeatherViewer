package com.kittynicky.app.servlet.auth;

import com.kittynicky.app.dto.UserDto;
import com.kittynicky.app.exception.PasswordNotVerifiedException;
import com.kittynicky.app.exception.UserNotFoundException;
import com.kittynicky.app.service.SessionService;
import com.kittynicky.app.service.UserService;
import com.kittynicky.app.servlet.MainServlet;
import com.kittynicky.app.util.ViewPath;
import com.kittynicky.app.util.WebServletPath;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@WebServlet(WebServletPath.SIGN_IN)
public class SignInServlet extends MainServlet {
    private final UserService userService = UserService.getInstance();
    private final SessionService sessionService = SessionService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        templateEngine.process(ViewPath.SIGN_IN, webContext, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Sign-in process has started");
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        try {
            Optional<UserDto> user = userService.login(login, password);
            user.ifPresent(dto -> onLoginSuccess(dto, resp));
        } catch (UserNotFoundException | PasswordNotVerifiedException e) {
            log.warn("Sign-in process has finished unsuccessfully");
            req.setAttribute("errorMessage", e.getMessage());
            doGet(req, resp);
        }
    }

    @SneakyThrows
    private void onLoginSuccess(UserDto userDto, HttpServletResponse resp) {
        sessionService.addCookie(userDto, resp);
        log.info("Sign-in processing has finished successfully");
        resp.sendRedirect(WebServletPath.HOME);
    }
}
