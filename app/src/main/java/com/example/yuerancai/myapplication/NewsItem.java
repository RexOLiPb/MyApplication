package com.example.yuerancai.myapplication;

/**
 * Created by yuerancai on 2017/11/24.
 */

public class NewsItem {
    private String newsHeading;
    private String author;
    private String date;
    private String url;


    public NewsItem(String newsHeading, String date, String author, String url) {
        this.date = date;
        this.newsHeading = newsHeading;
        this.author = author;
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public String getNewsHeading() {
        return newsHeading;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }
}
