package servlet.auth;

import com.kittynicky.app.dto.UserDto;
import com.kittynicky.app.entity.User;
import com.kittynicky.app.exception.PasswordNotVerifiedException;
import com.kittynicky.app.exception.UserNotFoundException;
import com.kittynicky.app.mapper.UserMapper;
import com.kittynicky.app.service.SessionService;
import com.kittynicky.app.service.UserService;
import com.kittynicky.app.util.BCryptUtils;
import com.kittynicky.app.util.ViewPath;
import com.kittynicky.app.util.WebServletPath;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.kittynicky.app.service.SessionService.COOKIE_SESSION_ATTR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SignInServletTest {
    @Mock
    private UserService userService;
    @Mock
    private SessionService sessionService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private TemplateEngine templateEngine;
    @Mock
    private WebContext webContext;
    @Spy
    private UserMapper userMapper = UserMapper.getInstance();

    @Test
    public void doPost_UserExistAndPasswordIsVerified_CreateOrUpdateSessionAndAddCookieAndSendRedirect() throws ServletException, IOException {
        String login = "Qwerty123";
        String password = "Qwerty123";
        User user = User.builder()
                .id(1)
                .login(login)
                .password(BCryptUtils.getHash(password))
                .build();
        Optional<UserDto> userDto = Optional.ofNullable(userMapper.mapFrom(user));
        UUID uuid = UUID.randomUUID();
        Cookie cookie = new Cookie(COOKIE_SESSION_ATTR, uuid.toString());
        cookie.setPath("/");
        cookie.setMaxAge(-1);

        when(request.getParameter("login")).thenReturn(login);
        when(request.getParameter("password")).thenReturn(password);
        when(userService.login(login, password)).thenReturn(userDto);
        sessionService.addCookie(userDto.get(), response);
        when(sessionService.updateOrSave(userDto.get())).thenReturn(uuid);
        response.addCookie(cookie);
        response.sendRedirect(WebServletPath.HOME);

        assertEquals(login, request.getParameter("login"));
        verify(request).getParameter("login");
        assertEquals(login, request.getParameter("password"));
        verify(request).getParameter("password");

        assertEquals(userDto, userService.login(login, password));
        verify(userService).login(login, password);

        verify(sessionService).addCookie(userDto.get(), response);
        assertEquals(uuid, sessionService.updateOrSave(userDto.get()));
        verify(sessionService).updateOrSave(userDto.get());
        verify(response).addCookie(cookie);
        verify(response).sendRedirect(WebServletPath.HOME);
    }

    @Test
    public void doPost_UserNotFound_ThrowUserNotFoundException() throws IOException {
        String login = "Qwerty123";
        String password = "Qwerty123";

        when(request.getParameter("login")).thenReturn(login);
        when(request.getParameter("password")).thenReturn(password);
        when(userService.login(login, password)).thenThrow(UserNotFoundException.class);
        UserNotFoundException exception = new UserNotFoundException("User with login '" + login + "' not found.");
        request.setAttribute("errorMessage", exception.getMessage());
        templateEngine.process(ViewPath.SIGN_IN, webContext, response.getWriter());

        assertEquals(login, request.getParameter("login"));
        verify(request).getParameter("login");

        assertEquals(login, request.getParameter("password"));
        verify(request).getParameter("password");

        assertThrows(UserNotFoundException.class, () -> userService.login(login, password));
        verify(userService).login(login, password);
        verify(request).setAttribute("errorMessage", "User with login '" + login + "' not found.");
        verify(templateEngine).process(ViewPath.SIGN_IN, webContext, response.getWriter());
    }

    @Test
    public void doPost_PasswordIsNotVerified_ThrowPasswordNotVerifiedException() throws IOException {
        String login = "Qwerty123";
        String password = "Qwerty123";

        when(request.getParameter("login")).thenReturn(login);
        when(request.getParameter("password")).thenReturn(password);
        when(userService.login(login, password)).thenThrow(PasswordNotVerifiedException.class);
        PasswordNotVerifiedException exception = new PasswordNotVerifiedException("Password isn't verified. Please, try again.");
        request.setAttribute("errorMessage", exception.getMessage());
        templateEngine.process(ViewPath.SIGN_IN, webContext, response.getWriter());

        assertEquals(login, request.getParameter("login"));
        verify(request).getParameter("login");

        assertEquals(login, request.getParameter("password"));
        verify(request).getParameter("password");

        assertThrows(PasswordNotVerifiedException.class, () -> userService.login(login, password));
        verify(userService).login(login, password);
        verify(request).setAttribute("errorMessage", "Password isn't verified. Please, try again.");
        verify(templateEngine).process(ViewPath.SIGN_IN, webContext, response.getWriter());
    }
}
