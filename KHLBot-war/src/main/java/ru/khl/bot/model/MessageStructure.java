package ru.khl.bot.model;

import java.util.List;

public class MessageStructure {

    private List<WallItem> wallItems;

    public List<WallItem> getWallItems() {
        return wallItems;
    }

    public void setWallItems(List<WallItem> wallItems) {
        this.wallItems = wallItems;
    }
}
