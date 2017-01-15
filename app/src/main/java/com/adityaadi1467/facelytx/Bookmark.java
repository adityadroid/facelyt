package com.adityaadi1467.facelytx;

/**
 * Created by adi on 30/12/16.
 */
public class Bookmark {
    String url;
    String title;

    public Bookmark(String title,String url)
    {
        this.title=title;
        this.url=url;

    }
    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
