package com.kittynicky.app.service;

import com.kittynicky.app.dao.SessionDao;
import com.kittynicky.app.dto.UserDto;
import com.kittynicky.app.entity.Session;
import com.kittynicky.app.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionService {
    public static final String COOKIE_SESSION_ATTR = "sessionId";
    private static final SessionService INSTANCE = new SessionService();
    private static final int DURATION_HOURS = 1;
    private final SessionDao sessionDao = SessionDao.getInstance();

    public static SessionService getInstance() {
        return INSTANCE;
    }

    public UUID updateOrSave(UserDto userDto) {
        UUID sessionId;
        User user = User.builder()
                .id(userDto.getId())
                .login(userDto.getLogin())
                .build();
        Optional<Session> session = sessionDao.findByUser(user);

        if (session.isPresent() && !isExpired(session.get().getExpiresAt())) {
            sessionId = sessionDao.update(Session.builder()
                    .id(session.get().getId())
                    .user(user)
                    .expiresAt(getExpiresAt())
                    .build());
        } else {
            sessionId = sessionDao.save(Session.builder()
                    .user(user)
                    .expiresAt(getExpiresAt())
                    .build());
        }
        return sessionId;
    }

    public boolean isExpired(LocalDateTime time) {
        return LocalDateTime.now().isAfter(time);
    }

    public Optional<Session> getSession(UUID sessionId) {
        return sessionDao.findById(sessionId);
    }

    public void addCookie(UserDto userDto, HttpServletResponse response) {
        UUID sessionId = updateOrSave(userDto);
        Cookie cookie = new Cookie(COOKIE_SESSION_ATTR, sessionId.toString());
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    public UUID delete(Session session) {
        return sessionDao.delete(session);
    }

    public void deleteCookie(HttpServletResponse resp) {
        Cookie cookie = new Cookie(COOKIE_SESSION_ATTR, null);
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
    }

    public LocalDateTime getExpiresAt() {
        return LocalDateTime.now().plusHours(DURATION_HOURS);
    }

    public void updateExpiredAt(Session session) {
        Session newSession = Session.builder()
                .id(session.getId())
                .user(session.getUser())
                .expiresAt(getNewExpiresAt())
                .build();
        sessionDao.update(newSession);
    }

    public void removeAllExpiredSessions() {
        sessionDao.removeAllExpiredSessions(LocalDateTime.now());
    }

    public LocalDateTime getNewExpiresAt() {
        return LocalDateTime.now().plusHours(DURATION_HOURS);
    }
}
