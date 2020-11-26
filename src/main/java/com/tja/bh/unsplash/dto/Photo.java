package com.tja.bh.unsplash.dto;

public class Photo {
    private String id;
    private int width;
    private int height;
    private String color;
    private Urls urls;
    private Links links;

    public Photo() {
        id = "";
        width = 0;
        height = 0;
        color = "";
        urls = new Urls();
        links = new Links();
    }

    public Photo(String id, int width, int height, String color, Urls urls, Links links) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.color = color;
        this.urls = urls;
        this.links = links;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setUrls(Urls urls) {
        this.urls = urls;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getId() {
        return id;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getColor() {
        return color;
    }

    public Urls getUrls() {
        return urls;
    }

    public Links getLinks() {
        return links;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id='" + id + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", color='" + color + '\'' +
                ", urls=" + urls +
                ", links=" + links +
                '}';
    }
}

