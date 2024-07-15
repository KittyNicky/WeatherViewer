package servlet.auth;

import com.kittynicky.app.entity.Session;
import com.kittynicky.app.entity.User;
import com.kittynicky.app.service.SessionService;
import com.kittynicky.app.util.Utils;
import com.kittynicky.app.util.WebServletPath;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class SignOutServletTest {
    @Mock
    private SessionService sessionService;
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;

    @Test
    public void doGet_CookieAndSessionExist_DeleteCookieAndSessionAndSendRedirect() throws IOException {
        UUID sessionId = UUID.randomUUID();
        User user = User.builder().id(1).login("Qwerty123").password("Qwerty123").build();
        Session s = Session.builder().id(sessionId).user(user).expiresAt(sessionService.getNewExpiresAt()).build();

        when(req.getCookies()).thenReturn(new Cookie[]{
                new Cookie(SessionService.COOKIE_SESSION_ATTR, sessionId.toString())
        });
        Optional<Cookie> cookie = Utils.getCookie(req.getCookies(), SessionService.COOKIE_SESSION_ATTR);
        when(sessionService.getSession(sessionId)).thenReturn(Optional.of(s));
        Optional<Session> session = sessionService.getSession(sessionId);
        sessionService.deleteCookie(resp);
        sessionService.delete(s);
        resp.sendRedirect(WebServletPath.HOME);

        assertTrue(cookie.isPresent());
        verify(req).getCookies();
        assertTrue(session.isPresent());
        assertEquals(sessionId, session.get().getId());
        verify(sessionService).getSession(sessionId);
        verify(sessionService).deleteCookie(resp);
        verify(sessionService).delete(s);
        verify(resp).sendRedirect(WebServletPath.HOME);
    }


    @Test
    public void doGet_CookieExistAndSessionNotExist_DeleteCookieAndSendRedirect() throws IOException {
        UUID sessionId = UUID.randomUUID();

        when(req.getCookies()).thenReturn(new Cookie[]{
                new Cookie(SessionService.COOKIE_SESSION_ATTR, sessionId.toString())
        });
        Optional<Cookie> cookie = Utils.getCookie(req.getCookies(), SessionService.COOKIE_SESSION_ATTR);
        when(sessionService.getSession(sessionId)).thenReturn(Optional.empty());
        Optional<Session> session = sessionService.getSession(UUID.fromString(cookie.get().getValue()));
        sessionService.deleteCookie(resp);
        resp.sendRedirect(WebServletPath.HOME);

        assertTrue(cookie.isPresent());
        verify(req).getCookies();
        verify(sessionService).getSession(UUID.fromString(cookie.get().getValue()));
        verify(sessionService).deleteCookie(resp);
        assertEquals(Optional.empty(), session);
        verify(resp).sendRedirect(WebServletPath.HOME);
    }

    @Test
    public void doGet_CookieNotExist_SendRedirect() throws IOException {
        when(req.getCookies()).thenReturn(null);
        resp.sendRedirect(WebServletPath.HOME);

        assertEquals(Utils.getCookie(req.getCookies(), SessionService.COOKIE_SESSION_ATTR), Optional.empty());
        verify(req).getCookies();
        verify(resp).sendRedirect(WebServletPath.HOME);
    }
}
