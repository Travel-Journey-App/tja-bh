package com.tja.bh.unsplash.dto;

public class Urls {
    private String raw;
    private String full;
    private String regular;
    private String small;
    private String thumb;

    public Urls() {
        this.raw = "";
        this.full = "";
        this.regular = "";
        this.small = "";
        this.thumb = "";
    }

    public Urls(String raw, String full, String regular, String small, String thumb) {
        this.raw = raw;
        this.full = full;
        this.regular = regular;
        this.small = small;
        this.thumb = thumb;
    }

    public void setFull(String full) {
        this.full = full;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public void setRegular(String regular) {
        this.regular = regular;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getFull() {
        return full;
    }

    public String getRaw() {
        return raw;
    }

    public String getRegular() {
        return regular;
    }

    public String getSmall() {
        return small;
    }

    public String getThumb() {
        return thumb;
    }

    @Override
    public String toString() {
        return "Urls{" +
                "raw='" + raw + '\'' +
                ", full='" + full + '\'' +
                ", regular='" + regular + '\'' +
                ", small='" + small + '\'' +
                ", thumb='" + thumb + '\'' +
                '}';
    }
}
