package com.qminder.burgers.qminder.dtos.fourSquare.photos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ResponseDto implements Serializable {
    @JsonProperty("photos")
    private PhotoHolderDto photoHolder;

    public List<Item> extractPictureItems(){
        return this.photoHolder
                .getGroups()
                .stream()
                .map(Group::getItems)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
