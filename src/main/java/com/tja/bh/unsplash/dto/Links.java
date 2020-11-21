package com.tja.bh.unsplash.dto;

public class Links {
    private String self;
    private String html;
    private String download;
    private String download_location;

    public Links() {
        this.self = "";
        this.html = "";
        this.download = "";
        this.download_location = "";
    }

    public Links(String self, String html, String download, String download_location) {
        this.self = self;
        this.html = html;
        this.download = download;
        this.download_location = download_location;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public void setDownload_location(String download_location) {
        this.download_location = download_location;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getDownload() {
        return download;
    }

    public String getDownload_location() {
        return download_location;
    }

    public String getHtml() {
        return html;
    }

    public String getSelf() {
        return self;
    }

    @Override
    public String toString() {
        return "Links{" +
                "self='" + self + '\'' +
                ", html='" + html + '\'' +
                ", download='" + download + '\'' +
                ", download_location='" + download_location + '\'' +
                '}';
    }
}
