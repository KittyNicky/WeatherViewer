package com.kittynicky.app.servlet;

import com.kittynicky.app.api.entity.dto.WeatherDto;
import com.kittynicky.app.mapper.UserMapper;
import com.kittynicky.app.service.SessionService;
import com.kittynicky.app.service.WeatherService;
import com.kittynicky.app.util.Utils;
import com.kittynicky.app.util.ViewPath;
import com.kittynicky.app.util.WebServletPath;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.kittynicky.app.service.SessionService.COOKIE_SESSION_ATTR;

@Slf4j
@WebServlet(WebServletPath.HOME)
public class HomeServlet extends MainServlet {

    private final SessionService sessionService = SessionService.getInstance();
    private final WeatherService weatherService = WeatherService.getInstance();
    private final UserMapper userMapper = UserMapper.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<Cookie> cookieSession = Utils.getCookie(req.getCookies(), COOKIE_SESSION_ATTR);
        cookieSession.flatMap(cookie -> sessionService.getSession(UUID.fromString(cookie.getValue())))
                .ifPresent(session -> {
                    log.info("Getting current weather for user '" + session.getUser().getLogin() + "'");
                    List<WeatherDto> currentWeather = weatherService.getCurrentWeather(userMapper.mapFrom(session.getUser()));
                    req.setAttribute("currentWeather", currentWeather);
                });
        templateEngine.process(ViewPath.HOME, webContext, resp.getWriter());
    }
}