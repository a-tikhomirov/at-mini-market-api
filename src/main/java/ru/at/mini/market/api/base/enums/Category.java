package ru.at.mini.market.api.base.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Category {
    FOOD(1, "Food"),
    ELECTRONIC(2, "Electronic");

    public final int id;
    public final String title;

    public static Category getCategoryById(int id) {
        for(Category e : values()) {
            if(e.id == id) return e;
        }
        throw new IllegalArgumentException("Not fount Category with id: " + id);
    }

}
