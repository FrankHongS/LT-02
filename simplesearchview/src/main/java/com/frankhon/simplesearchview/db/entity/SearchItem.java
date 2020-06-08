package com.frankhon.simplesearchview.db.entity;

/**
 * Created by Frank Hon on 2020-06-07 16:33.
 * E-mail: frank_hon@foxmail.com
 */
public class SearchItem {

    private int id;

    private String text;

    public SearchItem(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
