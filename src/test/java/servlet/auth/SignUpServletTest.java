package servlet.auth;

import com.kittynicky.app.dto.UserDto;
import com.kittynicky.app.entity.User;
import com.kittynicky.app.exception.ValidationException;
import com.kittynicky.app.mapper.UserMapper;
import com.kittynicky.app.service.SessionService;
import com.kittynicky.app.service.UserService;
import com.kittynicky.app.util.WebServletPath;
import com.kittynicky.app.validator.SignUpValidator;
import com.kittynicky.app.validator.ValidationResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SignUpServletTest {
    @Mock
    private UserService userService;
    @Mock
    private SessionService sessionService;
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @Spy
    private final SignUpValidator signUpValidator = SignUpValidator.getInstance();
    @Spy
    private final UserMapper userMapper = UserMapper.getInstance();

    @Test
    public void doPost_LoginAndPasswordAreValid_CreateSessionAndUserAndSendRedirect() throws IOException {
        String login = "Qwerty123";
        String password = "Qwerty123";
        Integer userId = 1;

        when(req.getParameter("login")).thenReturn(login);
        when(req.getParameter("password")).thenReturn(password);
        when(userService.create(login, password)).thenReturn(userId);
        User user = User.builder().id(userId).login(login).password(password).build();
        when(userService.getById(userId)).thenReturn(Optional.of(user));
        UserDto userDto = userMapper.mapFrom(user);
        sessionService.addCookie(userDto, resp);
        resp.sendRedirect(WebServletPath.HOME);

        assertEquals(login, req.getParameter("login"));
        verify(req).getParameter("login");
        assertEquals(login, req.getParameter("password"));
        verify(req).getParameter("password");
        assertEquals(userId, userService.create(login, password));
        verify(userService).create(login, password);
        assertEquals(Optional.of(user), userService.getById(userId));
        verify(userService).getById(userId);
        verify(sessionService).addCookie(userDto, resp);
        verify(resp).sendRedirect(WebServletPath.HOME);
    }

    @Test
    public void doPost_LoginAndPasswordAreInValid_ThrowValidationExceptionAndSetAttribute() throws IOException {
        String login = "qwe";
        String password = "qwerty";
        Integer userId = 1;

        when(req.getParameter("login")).thenReturn(login);
        when(req.getParameter("password")).thenReturn(password);
        when(userService.create(login, password)).thenThrow(ValidationException.class);
        User user = User.builder().id(userId).login(login).password(password).build();
        ValidationResult validationResult = signUpValidator.isValid(user);
        ValidationException exception = new ValidationException(validationResult.getErrors());
        req.setAttribute("errorMessage", exception.getMessage());

        assertEquals(login, req.getParameter("login"));
        verify(req).getParameter("login");
        assertEquals(password, req.getParameter("password"));
        verify(req).getParameter("password");
        assertThrows(ValidationException.class, () -> userService.create(login, password));
        verify(userService).create(login, password);
        assertEquals(
                "Login must be at least 6 characters, " +
                "password must contain digit, " +
                "password must contain upper case letter",
                exception.getMessage()
        );
        verify(req).setAttribute("errorMessage", exception.getMessage());
    }
}
