package servlet;

import com.kittynicky.app.api.entity.dto.WeatherDto;
import com.kittynicky.app.entity.Session;
import com.kittynicky.app.entity.User;
import com.kittynicky.app.mapper.UserMapper;
import com.kittynicky.app.service.SessionService;
import com.kittynicky.app.service.WeatherService;
import com.kittynicky.app.util.Utils;
import com.kittynicky.app.util.ViewPath;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.kittynicky.app.service.SessionService.COOKIE_SESSION_ATTR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HomeServletTest {
    @Mock
    private SessionService sessionService;
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @Mock
    private WeatherService weatherService;
    @Mock
    private TemplateEngine templateEngine;
    @Mock
    private WebContext webContext;
    @Spy
    private UserMapper userMapper = UserMapper.getInstance();

    @Test
    public void doGet_CookieAndSessionExist_SetAttributes() throws IOException {
        UUID sessionId = UUID.randomUUID();
        User user = User.builder().id(1).login("Qwerty123").password("Qwerty123").build();
        Session s = Session.builder().id(sessionId).user(user).expiresAt(sessionService.getNewExpiresAt()).build();
        List<WeatherDto> currentWeather = List.of(WeatherDto.builder().build());

        when(req.getCookies()).thenReturn(new Cookie[]{
                new Cookie(SessionService.COOKIE_SESSION_ATTR, sessionId.toString())
        });
        Optional<Cookie> cookie = Utils.getCookie(req.getCookies(), COOKIE_SESSION_ATTR);
        when(sessionService.getSession(sessionId)).thenReturn(Optional.of(s));
        Optional<Session> session = sessionService.getSession(UUID.fromString(cookie.get().getValue()));
        when(weatherService.getCurrentWeather(userMapper.mapFrom(s.getUser()))).thenReturn(currentWeather);
        req.setAttribute("currentWeather", currentWeather);
        templateEngine.process(ViewPath.HOME, webContext, resp.getWriter());

        assertTrue(cookie.isPresent());
        verify(req).getCookies();
        assertTrue(session.isPresent());
        assertEquals(sessionId, session.get().getId());
        verify(sessionService).getSession(sessionId);
        assertEquals(currentWeather, weatherService.getCurrentWeather(userMapper.mapFrom(s.getUser())));
        verify(weatherService).getCurrentWeather(userMapper.mapFrom(s.getUser()));
        verify(req).setAttribute("currentWeather", currentWeather);
        verify(templateEngine).process(ViewPath.HOME, webContext, resp.getWriter());
    }

    @Test
    public void doGet_CookieNotExist_doGet() throws IOException {
        when(req.getCookies()).thenReturn(null);
        templateEngine.process(ViewPath.HOME, webContext, resp.getWriter());

        assertEquals(Utils.getCookie(req.getCookies(), SessionService.COOKIE_SESSION_ATTR), Optional.empty());
        verify(req).getCookies();
        verify(templateEngine).process(ViewPath.HOME, webContext, resp.getWriter());
    }

    @Test
    public void doGet_SessionNotExist_doGet() throws IOException {
        UUID sessionId = UUID.randomUUID();

        when(req.getCookies()).thenReturn(new Cookie[]{
                new Cookie(SessionService.COOKIE_SESSION_ATTR, sessionId.toString())
        });
        Optional<Cookie> cookie = Utils.getCookie(req.getCookies(), COOKIE_SESSION_ATTR);
        when(sessionService.getSession(sessionId)).thenReturn(Optional.empty());
        Optional<Session> session = sessionService.getSession(UUID.fromString(cookie.get().getValue()));
        templateEngine.process(ViewPath.HOME, webContext, resp.getWriter());

        assertTrue(cookie.isPresent());
        verify(req).getCookies();
        verify(sessionService).getSession(sessionId);
        assertEquals(Optional.empty(), session);
        verify(templateEngine).process(ViewPath.HOME, webContext, resp.getWriter());
    }
}
