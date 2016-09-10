package com.summertaker.akb48guide.data;

import java.io.Serializable;

public class MenuData implements Serializable {

    private static final long serialVersionUID = 1L;

    String id;
    String title;
    String url;
    int drawable;
    int faBackIcon;
    int faBackColor;
    int faTextIcon;
    int faTextColor;

    public MenuData() {

    }

    public MenuData(String key, String title, int drawable) {
        this.id = key;
        this.title = title;
        this.drawable = drawable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getFaBackIcon() {
        return faBackIcon;
    }

    public void setFaBackIcon(int faBackIcon) {
        this.faBackIcon = faBackIcon;
    }

    public int getFaBackColor() {
        return faBackColor;
    }

    public void setFaBackColor(int faBackColor) {
        this.faBackColor = faBackColor;
    }

    public int getFaTextIcon() {
        return faTextIcon;
    }

    public void setFaTextIcon(int faTextIcon) {
        this.faTextIcon = faTextIcon;
    }

    public int getFaTextColor() {
        return faTextColor;
    }

    public void setFaTextColor(int faTextColor) {
        this.faTextColor = faTextColor;
    }
}
