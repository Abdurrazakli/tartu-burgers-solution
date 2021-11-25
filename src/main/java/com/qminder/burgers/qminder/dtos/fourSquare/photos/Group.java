package com.qminder.burgers.qminder.dtos.fourSquare.photos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Group implements Serializable {
    @JsonProperty("items")
    private List<Item> items;
}
