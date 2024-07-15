package servlet;

import com.kittynicky.app.api.entity.dto.WeatherDto;
import com.kittynicky.app.service.WeatherService;
import com.kittynicky.app.util.ViewPath;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GeoCodingWeatherServletTest {
    @Mock
    private HttpServletRequest req;
    @Mock
    private WeatherService weatherService;
    @Mock
    private WebContext webContext;
    @Mock
    private TemplateEngine templateEngine;
    @Mock
    private HttpServletResponse resp;

    @Test
    public void doPost_GetGeocodingWeather_SetGeoCodingWeatherAndCityVariables() throws IOException {
        String city = "Saint Petersburg";
        when(req.getParameter("city")).thenReturn(city);
        List<WeatherDto> geoCodingWeather = List.of(WeatherDto.builder().build());
        when(weatherService.getGeocodingWeather(city)).thenReturn(geoCodingWeather);
        webContext.setVariable("geoCodingWeather", geoCodingWeather);
        webContext.setVariable("city", city);
        templateEngine.process(ViewPath.WEATHER, webContext, resp.getWriter());

        assertEquals(city, req.getParameter("city"));
        verify(req).getParameter("city");
        assertEquals(geoCodingWeather, weatherService.getGeocodingWeather(city));
        verify(weatherService).getGeocodingWeather(city);
        verify(webContext).setVariable("geoCodingWeather", geoCodingWeather);
        verify(webContext).setVariable("city", city);
        verify(templateEngine).process(ViewPath.WEATHER, webContext, resp.getWriter());
    }
}
