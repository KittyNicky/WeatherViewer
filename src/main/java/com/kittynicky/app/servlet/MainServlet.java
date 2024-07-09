package com.kittynicky.app.servlet;

import com.kittynicky.app.entity.Session;
import com.kittynicky.app.service.SessionService;
import com.kittynicky.app.util.Utils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.kittynicky.app.service.SessionService.COOKIE_SESSION_ATTR;
import static com.kittynicky.app.util.ViewPath.ERROR;

@Slf4j
public class MainServlet extends HttpServlet {
    private final SessionService sessionService = SessionService.getInstance();
    protected ITemplateEngine templateEngine;
    protected IWebExchange webExchange;
    protected WebContext webContext;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.templateEngine = (TemplateEngine) getServletContext().getAttribute(ThymeleafConfig.TEMPLATE_ENGINE_ATTR);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        this.webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange((HttpServletRequest) req, (HttpServletResponse) res);
        this.webContext = new WebContext(webExchange, req.getLocale());

        setResponseHeader((HttpServletResponse) res);

        try {
            super.service(req, res);
        } catch (Exception e) {
            log.warn(e.getMessage());
            templateEngine.process(ERROR, webContext, res.getWriter());
        }
    }

    private void setResponseHeader(HttpServletResponse res) {
        res.setContentType("text/html;charset=UTF-8");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Cache-Control", "no-cache");
        res.setDateHeader("Expires", 0);
    }

    public Optional<String> getLogin(HttpServletRequest request) {
        Optional<Cookie> cookie = Utils.getCookie(request.getCookies(), COOKIE_SESSION_ATTR);
        if (cookie.isPresent()) {
            Optional<Session> session = sessionService.getSession(UUID.fromString(cookie.get().getValue()));
            if (session.isPresent()) {
                return Optional.of(session.get().getUser().getLogin());
            }
        }
        return Optional.empty();
    }
}