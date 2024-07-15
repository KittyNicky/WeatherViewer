package servlet;

import com.kittynicky.app.api.entity.dto.DailyForecastWeatherDto;
import com.kittynicky.app.api.entity.dto.HourlyForecastWeatherDto;
import com.kittynicky.app.entity.Location;
import com.kittynicky.app.mapper.DailyForecastWeatherMapper;
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
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ForecastWeatherServletTest {
    @Mock
    private HttpServletRequest req;
    @Mock
    private HttpServletResponse resp;
    @Mock
    private WeatherService weatherService;
    @Mock
    private DailyForecastWeatherMapper dailyForecastWeatherMapper;
    @Mock
    private TemplateEngine templateEngine;
    @Mock
    private WebContext webContext;

    @Test
    public void doPost_GetHourlyAndDailyForecast_SetHourlyAndDailyForecastVariables() throws IOException {
        String city = "Saint Petersburg";
        BigDecimal latitude = BigDecimal.valueOf(59.9387);
        BigDecimal longitude = BigDecimal.valueOf(30.3162);
        Location location = Location.builder().name(city).longitude(longitude).latitude(latitude).build();
        List<HourlyForecastWeatherDto> hourlyForecastWeather = List.of(HourlyForecastWeatherDto.builder().build());
        List<DailyForecastWeatherDto> dailyForecastWeather = List.of(DailyForecastWeatherDto.builder().build());

        when(req.getParameter("city")).thenReturn(city);
        when(req.getParameter("latitude")).thenReturn(String.valueOf(latitude));
        when(req.getParameter("longitude")).thenReturn(String.valueOf(longitude));
        when(weatherService.getHourlyForecast(location)).thenReturn(hourlyForecastWeather);
        when(dailyForecastWeatherMapper.mapFrom(hourlyForecastWeather)).thenReturn(dailyForecastWeather);
        req.setAttribute("hourlyForecast", hourlyForecastWeather);
        req.setAttribute("dailyForecast", dailyForecastWeather);
        templateEngine.process(ViewPath.FORECAST, webContext, resp.getWriter());

        assertEquals(city, req.getParameter("city"));
        verify(req).getParameter("city");
        assertEquals(String.valueOf(latitude), req.getParameter("latitude"));
        verify(req).getParameter("latitude");
        assertEquals(String.valueOf(longitude), req.getParameter("longitude"));
        verify(req).getParameter("longitude");
        assertEquals(hourlyForecastWeather, weatherService.getHourlyForecast(location));
        verify(weatherService).getHourlyForecast(location);
        assertEquals(dailyForecastWeather, dailyForecastWeatherMapper.mapFrom(hourlyForecastWeather));
        verify(dailyForecastWeatherMapper).mapFrom(hourlyForecastWeather);
        verify(req).setAttribute("hourlyForecast", hourlyForecastWeather);
        verify(req).setAttribute("dailyForecast", dailyForecastWeather);
        verify(templateEngine).process(ViewPath.FORECAST, webContext, resp.getWriter());
    }
}
