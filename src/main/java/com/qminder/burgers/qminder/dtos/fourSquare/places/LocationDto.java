package com.qminder.burgers.qminder.dtos.fourSquare.places;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.StringJoiner;

@Data
public class LocationDto implements Serializable {
    @JsonProperty("address")
    private String address = "";
    @JsonProperty("cross_street")
    private String crossStreet = "";

    public String getFullAddress() {
        StringJoiner stringJoiner = new StringJoiner(",");
        stringJoiner.add(this.address).add(this.crossStreet);
        return stringJoiner.toString();
    }
}
