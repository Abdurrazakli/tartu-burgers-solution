package com.qminder.burgers.qminder.dtos.fourSquare.photos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class Item implements Serializable, Comparable<Item> {
    @JsonProperty("id")
    private String id;
    @JsonProperty("createdAt")
    private Long createdAt;
    @JsonProperty("prefix")
    private String prefix;
    @JsonProperty("suffix")
    private String suffix;

    public Item(String id, Long createdAt, String prefix, String suffix) {
        this.id = id;
        this.createdAt = createdAt;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public Item() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return Instant.ofEpochSecond(createdAt);
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getPictureUrl(){
        return String.format("%s600x600%s",prefix,suffix);
    }
    @Override
    public int compareTo(Item o) {
        return this.getCreatedAt().compareTo(o.getCreatedAt());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id.equals(item.id)
                && createdAt.equals(item.createdAt)
                && getPictureUrl().equals(item.getPictureUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, prefix, suffix);
    }
}
