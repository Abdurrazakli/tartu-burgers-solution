package com.qminder.burgers.qminder.dtos.fourSquare.photos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PhotoHolderDto implements Serializable {
    @JsonProperty("groups")
    private List<Group> groups;
}
