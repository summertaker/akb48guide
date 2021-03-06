package com.summertaker.akb48guide.data;

import android.util.Log;

import com.summertaker.akb48guide.R;
import com.summertaker.akb48guide.common.Config;

import java.io.Serializable;

public class GroupData implements Serializable {

    private static final long serialVersionUID = 1L;

    String id;
    String name;
    int image;
    String url;
    String mobileUrl;
    String mobileUrlOfAll;
    String rawPhotoUrl;
    boolean isLocked;

    public GroupData() {

    }

    public GroupData(String id, String name, int image, String url, String rawPhotoUrl) {
        super();
        this.id = id;
        this.name = name;
        this.image = image;
        this.url = url;
        this.rawPhotoUrl = rawPhotoUrl;
    }

    public GroupData(String id, String name, int image, String url, String mobileUrl, String mobileUrlOfAll, String rawPhotoUrl, boolean isLocked) {
        super();
        this.id = id;
        this.name = name;
        this.image = image;
        this.url = url;
        this.mobileUrl = mobileUrl;
        this.mobileUrlOfAll = mobileUrlOfAll;
        this.rawPhotoUrl = rawPhotoUrl;
        this.isLocked = isLocked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

    public String getMobileUrlOfAll() {
        return mobileUrlOfAll;
    }

    public void setMobileUrlOfAll(String mobileUrlOfAll) {
        this.mobileUrlOfAll = mobileUrlOfAll;
    }

    public String getRawPhotoUrl() {
        return rawPhotoUrl;
    }

    public void setRawPhotoUrl(String rawPhotoUrl) {
        this.rawPhotoUrl = rawPhotoUrl;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public String getString() {
        return this.id + " / " + this.name + " / " + this.image + " / " + this.url + " / " + this.mobileUrl;
    }
}
