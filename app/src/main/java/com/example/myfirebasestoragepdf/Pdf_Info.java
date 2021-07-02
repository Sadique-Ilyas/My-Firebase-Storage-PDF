package com.example.myfirebasestoragepdf;

public class Pdf_Info {
    public String name, url;

    public Pdf_Info() {
    }

    public Pdf_Info(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
