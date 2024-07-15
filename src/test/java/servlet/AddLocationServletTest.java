package servlet;

import com.kittynicky.app.dto.UserDto;
import com.kittynicky.app.entity.Location;
import com.kittynicky.app.entity.User;
import com.kittynicky.app.service.LocationService;
import com.kittynicky.app.service.UserService;
import com.kittynicky.app.servlet.AddLocationServlet;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddLocationServletTest {
    @Mock
    private LocationService locationService;
    @Mock
    private UserService userService;
    @Mock
    private AddLocationServlet addLocationServlet;
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;

    @Test
    public void doPost_UserExist_AddLocationAndSendRedirect() throws IOException {
        Optional<String> login = Optional.of("Qwerty123");
        Optional<UserDto> user = Optional.of(UserDto.builder().id(1).login(login.get()).build());
        String city = "Saint Petersburg";
        BigDecimal latitude = BigDecimal.valueOf(59.9387);
        BigDecimal longitude = BigDecimal.valueOf(30.3162);
        Location location = Location.builder()
                .name(city)
                .user(User.builder()
                        .id(user.get().getId())
                        .login(user.get().getLogin())
                        .build())
                .latitude(latitude)
                .longitude(longitude)
                .build();

        when(addLocationServlet.getLogin(req)).thenReturn(login);
        when(userService.getByLogin(login.get())).thenReturn(user);
        when(req.getParameter("city")).thenReturn(city);
        when(req.getParameter("latitude")).thenReturn(String.valueOf(latitude));
        when(req.getParameter("longitude")).thenReturn(String.valueOf(longitude));
        when(locationService.add(location)).thenReturn(1);
        resp.sendRedirect(HOME);

        assertEquals(login, addLocationServlet.getLogin(req));
        verify(addLocationServlet).getLogin(req);
        assertEquals(user, userService.getByLogin(login.get()));
        verify(userService).getByLogin(login.get());
        assertEquals(city, req.getParameter("city"));
        verify(req).getParameter("city");
        assertEquals(String.valueOf(latitude), req.getParameter("latitude"));
        verify(req).getParameter("latitude");
        assertEquals(String.valueOf(longitude), req.getParameter("longitude"));
        verify(req).getParameter("longitude");
        assertEquals(1, locationService.add(location));
        verify(locationService).add(location);
        verify(resp).sendRedirect(HOME);
    }

    @Test
    public void doPost_LoginNotFound_ThrowUserNotFoundException() {
        Optional<String> login = Optional.empty();

        when(addLocationServlet.getLogin(req)).thenReturn(Optional.empty());

        assertEquals(login, addLocationServlet.getLogin(req));
    }

    @Test
    public void doPost_UserNotFound_SendRedirect() throws IOException {
        Optional<String> login = Optional.of("Qwerty123");
        Optional<UserDto> user = Optional.of(UserDto.builder().id(1).login(login.get()).build());
        String city = "Saint Petersburg";
        BigDecimal latitude = BigDecimal.valueOf(59.9387);
        BigDecimal longitude = BigDecimal.valueOf(30.3162);

        when(addLocationServlet.getLogin(req)).thenReturn(login);
        when(userService.getByLogin(login.get())).thenReturn(user);
        when(req.getParameter("city")).thenReturn(city);
        when(req.getParameter("latitude")).thenReturn(String.valueOf(latitude));
        when(req.getParameter("longitude")).thenReturn(String.valueOf(longitude));
        resp.sendRedirect(HOME);

        assertEquals(login, addLocationServlet.getLogin(req));
        verify(addLocationServlet).getLogin(req);
        assertEquals(user, userService.getByLogin(login.get()));
        verify(userService).getByLogin(login.get());
        assertEquals(city, req.getParameter("city"));
        verify(req).getParameter("city");
        assertEquals(String.valueOf(latitude), req.getParameter("latitude"));
        verify(req).getParameter("latitude");
        assertEquals(String.valueOf(longitude), req.getParameter("longitude"));
        verify(req).getParameter("longitude");
        verify(resp).sendRedirect(HOME);
    }
}
