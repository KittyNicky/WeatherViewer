package com.kittynicky.app.servlet;

import com.kittynicky.app.dto.UserDto;
import com.kittynicky.app.exception.UserNotFoundException;
import com.kittynicky.app.service.LocationService;
import com.kittynicky.app.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static com.kittynicky.app.util.WebServletPath.HOME;
import static com.kittynicky.app.util.WebServletPath.REMOVE_LOCATION;

@Slf4j
@WebServlet(REMOVE_LOCATION)
public class RemoveLocationServlet extends MainServlet {
    private final LocationService locationService = LocationService.getInstance();
    private final UserService userService = UserService.getInstance();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Removing location process has started");
        Optional<String> login = getLogin(req);
        if (login.isEmpty()) {
            throw new UserNotFoundException("User with login '" + login + "' not found. Adding a location has been interrupted");
        }
        Optional<UserDto> user = userService.getByLogin(login.get());
        BigDecimal latitude = BigDecimal.valueOf(Double.parseDouble(req.getParameter("latitude")));
        BigDecimal longitude = BigDecimal.valueOf(Double.parseDouble(req.getParameter("longitude")));

        user.ifPresent(userDto -> locationService.remove(userDto, latitude, longitude));

        log.info("Removing location process has finished successfully");
        resp.sendRedirect(HOME);
    }
}
