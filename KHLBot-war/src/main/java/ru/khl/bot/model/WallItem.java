package ru.khl.bot.model;

import java.util.List;

public class WallItem {

    private List<Item> itemList;

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public List<Item> getItemList() {
        return itemList;
    }
}
