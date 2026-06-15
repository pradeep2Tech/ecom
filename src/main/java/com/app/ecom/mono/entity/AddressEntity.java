package com.app.ecom.mono.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "t_address")
public class AddressEntity {

    @Id
    private Long id;

    private String street;
    private String city;
    private String state;
    private String county;
    private String zipcode;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private UserEntity user;
}
