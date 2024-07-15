package com.kittynicky.app.service;

import com.kittynicky.app.dao.LocationDao;
import com.kittynicky.app.dto.UserDto;
import com.kittynicky.app.entity.Location;
import com.kittynicky.app.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationService {
    private static final LocationService INSTANCE = new LocationService();
    private final LocationDao locationDao = LocationDao.getInstance();

    public static LocationService getInstance() {
        return INSTANCE;
    }

    public Integer add(Location location) {
        return locationDao.save(location);
    }

    public List<Location> get(User user) {
        return locationDao.findByUser(user);
    }

    public void remove(UserDto userDto, BigDecimal latitude, BigDecimal longitude) {
        User user = User.builder()
                .id(userDto.getId())
                .login(userDto.getLogin())
                .build();
        Optional<Location> location = locationDao.findByUserAndLatitudeAndLongitude(user, latitude, longitude);
        location.ifPresent(locationDao::delete);
    }

    public Optional<Location> get(User user, BigDecimal latitude, BigDecimal longitude) {
        return locationDao.findByUserAndLatitudeAndLongitude(user, latitude, longitude);
    }

}
