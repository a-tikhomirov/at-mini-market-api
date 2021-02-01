package ru.at.mini.market.api.base.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Category {
    FOOD(1, "Food"),
    ELECTRONICS(2, "Electronic");

    public final int id;
    public final String title;
}
