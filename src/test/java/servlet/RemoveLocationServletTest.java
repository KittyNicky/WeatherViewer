package servlet;

import com.kittynicky.app.dto.UserDto;
import com.kittynicky.app.service.LocationService;
import com.kittynicky.app.service.UserService;
import com.kittynicky.app.servlet.RemoveLocationServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static com.kittynicky.app.util.WebServletPath.HOME;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RemoveLocationServletTest {
    @Mock
    private LocationService locationService;
    @Mock
    private UserService userService;
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @Mock
    private RemoveLocationServlet removeLocationServlet;

    @Test
    public void doPost_UserExist_RemoveLocationAndSendRedirect() throws IOException {
        Optional<String> login = Optional.of("Qwerty123");
        Optional<UserDto> user = Optional.of(UserDto.builder().id(1).login(login.get()).build());
        BigDecimal latitude = BigDecimal.valueOf(59.9387);
        BigDecimal longitude = BigDecimal.valueOf(30.3162);

        when(removeLocationServlet.getLogin(req)).thenReturn(login);
        when(userService.getByLogin(login.get())).thenReturn(user);
        when(req.getParameter("latitude")).thenReturn(String.valueOf(latitude));
        when(req.getParameter("longitude")).thenReturn(String.valueOf(longitude));
        locationService.remove(user.get(), latitude, longitude);
        resp.sendRedirect(HOME);

        assertEquals(login, removeLocationServlet.getLogin(req));
        verify(removeLocationServlet).getLogin(req);
        assertEquals(user, userService.getByLogin(login.get()));
        verify(userService).getByLogin(login.get());
        assertEquals(String.valueOf(latitude), req.getParameter("latitude"));
        verify(req).getParameter("latitude");
        assertEquals(String.valueOf(longitude), req.getParameter("longitude"));
        verify(req).getParameter("longitude");
        verify(locationService).remove(user.get(), latitude, longitude);
        verify(resp).sendRedirect(HOME);
    }

    @Test
    public void doPost_LoginNotFound_ThrowUserNotFoundException() {
        Optional<String> login = Optional.empty();
        when(removeLocationServlet.getLogin(req)).thenReturn(Optional.empty());

        assertEquals(login, removeLocationServlet.getLogin(req));
    }

    @Test
    public void doPost_UserNotFound_SendRedirect() throws IOException {
        Optional<String> login = Optional.of("Qwerty123");
        BigDecimal latitude = BigDecimal.valueOf(59.9387);
        BigDecimal longitude = BigDecimal.valueOf(30.3162);

        when(removeLocationServlet.getLogin(req)).thenReturn(login);
        when(userService.getByLogin(login.get())).thenReturn(Optional.empty());
        when(req.getParameter("latitude")).thenReturn(String.valueOf(latitude));
        when(req.getParameter("longitude")).thenReturn(String.valueOf(longitude));
        resp.sendRedirect(HOME);

        assertEquals(login, removeLocationServlet.getLogin(req));
        verify(removeLocationServlet).getLogin(req);
        assertEquals(Optional.empty(), userService.getByLogin(login.get()));
        verify(userService).getByLogin(login.get());
        assertEquals(String.valueOf(latitude), req.getParameter("latitude"));
        verify(req).getParameter("latitude");
        assertEquals(String.valueOf(longitude), req.getParameter("longitude"));
        verify(req).getParameter("longitude");
        verify(resp).sendRedirect(HOME);
    }
}
