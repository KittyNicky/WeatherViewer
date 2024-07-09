package com.kittynicky.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "public",
        name = "locations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uix_locations_user_id_latitude_longitude", columnNames = {"user_id", "latitude", "longitude"})
        })
public class Location {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_locations_user_id"))
    private User user;

    @Column(name = "latitude", precision = 10, scale = 4)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 4)
    private BigDecimal longitude;
}
