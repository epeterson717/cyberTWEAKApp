/*
 Code for Browser Activity modified from "simple_web_browser_app"
 Created by Red Ayoub (redayoub47) on 5/24/18.
 https://github.com/redayoub47/simple_web_browser_app
 */

package com.list.nasro.lionsbrowser;


public class WebPage {
    private int id;
    private String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public WebPage() {
    }

    public WebPage(int id, String url) {
        this.id = id;
        this.url = url;
    }
}
