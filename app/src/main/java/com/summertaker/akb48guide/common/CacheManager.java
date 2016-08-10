package com.summertaker.akb48guide.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CacheManager {

    protected String mTag;

    protected SharedPreferences mSharedPreferences;
    protected SharedPreferences.Editor mSharedEditor;

    protected String mDateFormatString = "yyyy-MM-dd HH:mm:ss";

    public CacheManager(Context context) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mSharedPreferences = context.getSharedPreferences(Config.USER_PREFERENCE_KEY, Context.MODE_PRIVATE);
    }

    public CacheManager(SharedPreferences sharedPreferences) {
        this.mTag = "===== " + this.getClass().getSimpleName();
        this.mSharedPreferences = sharedPreferences;
    }

    public String load(String cacheKey) {
        return load(cacheKey, Config.CACHE_EXPIRE_TIME);
    }

    public String load(String cacheKey, int expireMinutes) {
        String data = null;
        String jsonString = mSharedPreferences.getString(cacheKey, "");
        if (jsonString.isEmpty()) {
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String cacheDate = jsonObject.getString("cacheDate");
            String today = Util.getToday(mDateFormatString);

            boolean isValid = true;
            if (expireMinutes > 0) {
                isValid = isValidCacheDate(cacheDate, today);
            }
            if (isValid) {
                data = jsonObject.getString("data");
            }
            //Log.e(mTag, " - isValid: " + isValid + " / cacheDate: " + cacheDate + ", today: " +today);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(mTag, e.getMessage());
        }

        return data;
    }

    public JSONObject loadJsonObject(String cacheKey, int expireMinutes) {
        String jsonString = mSharedPreferences.getString(cacheKey, "");
        //Log.e(mTag, "jsonString: " + jsonString);
        if (jsonString.isEmpty()) {
            return null;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            String cacheDate = jsonObject.getString("cacheDate");

            boolean isValid = true;
            if (expireMinutes > 0) {
                String today = Util.getToday(mDateFormatString);
                isValid = isValidCacheDate(cacheDate, today);
            }
            if (!isValid) {
                jsonObject = null;
            }
            //Log.e(mTag, " - isValid: " + isValid + " / cacheDate: " + cacheDate + ", today: " +today);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(mTag, e.getMessage());
        }

        return jsonObject;
    }

    public void save(String cacheKey, String data) {
        if (data == null || data.isEmpty() || data.equals("[]")) {
            //return;
            data = "";
        }

        String today = Util.getToday(mDateFormatString);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cacheDate", today);
            jsonObject.put("data", data);
            //Log.e(mTag, jsonObject.toString());

            mSharedEditor = mSharedPreferences.edit();
            mSharedEditor.putString(cacheKey, jsonObject.toString());
            mSharedEditor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(mTag, e.getMessage());
        }
    }

    public void save(String cacheKey, JSONArray data) {
        String today = Util.getToday(mDateFormatString);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cacheDate", today);
            jsonObject.put("data", data);
            //Log.e(mTag, jsonObject.toString());

            mSharedEditor = mSharedPreferences.edit();
            mSharedEditor.putString(cacheKey, jsonObject.toString());
            mSharedEditor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(mTag, e.getMessage());
        }
    }

    public boolean loadSnsCache(String cacheKey, ArrayList<WebData> dataList) {
        //Log.e(mTag, "loadSnsCache()......... " + cacheKey);

        String jsonString = mSharedPreferences.getString(cacheKey, "");
        //Log.e(mTag, " - Cache data: " + jsonString);
        if (jsonString.isEmpty()) {
            return false;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String cacheDate = jsonObject.getString("cacheDate");
            String today = Util.getToday(mDateFormatString);

            boolean isValid = isValidCacheDate(cacheDate, today);
            //Log.e(mTag, " - isValid: " + isValid + " / cacheDate: " + cacheDate + ", today: " +today);

            if (isValid) {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                //Log.e(mTag, "jsonArray.toString(): " + jsonArray.toString());
                dataList.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    WebData data = new WebData();
                    //data.setSiteKey(Util.getString(object, "siteKey"));
                    data.setId(Util.getString(object, "id"));
                    data.setUserId(Util.getString(object, "userId"));
                    data.setTitle(Util.getString(object, "title"));
                    data.setContent(Util.getString(object, "content"));
                    data.setDate(Util.getString(object, "date"));
                    data.setUrl(Util.getString(object, "url"));
                    data.setImageUrl(Util.getString(object, "imageUrl"));
                    data.setThumbnailUrl(Util.getString(object, "thumbnailUrl"));

                    dataList.add(data);
                }

                //Log.e(mTag, "- dataList.size(): " + dataList.size());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(mTag, e.getMessage());
        }

        return dataList.size() > 0;
    }

    public void saveSnsCache(String cacheKey, ArrayList<WebData> dataList) {
        //Log.e(mTag, "saveSnsCache()......... " + cacheKey);

        if (dataList == null || dataList.size() == 0) {
            return;
        }

        String today = Util.getToday(mDateFormatString);
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            for (WebData data : dataList) {
                JSONObject object = new JSONObject();
                //object.put("siteKey", data.getSiteKey());
                object.put("id", data.getId());
                object.put("userId", data.getUserId());
                object.put("title", data.getTitle());
                object.put("content", data.getContent());
                object.put("date", data.getDate());
                object.put("url", data.getUrl());
                object.put("imageUrl", data.getImageUrl());
                object.put("thumbnailUrl", data.getThumbnailUrl());
                jsonArray.put(object);
            }

            jsonObject.put("cacheDate", today);
            jsonObject.put("items", jsonArray);

            //Log.e(mTag, jsonObject.toString());

            mSharedEditor = mSharedPreferences.edit();
            mSharedEditor.putString(cacheKey, jsonObject.toString());
            mSharedEditor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(mTag, e.getMessage());
        }
    }

    /**
     * 블로그 캐쉬 불러오기
     */
    /*
    public boolean loadBlogCache(ArrayList<SiteData> dataList) {
        //Log.e(mTag, "loadGalleryCache().........");

        boolean isValidCache = false;

        String jsonString = mSharedPreferences.getString(mCacheKeyBlog, "");
        //Log.e(mTag, "jsonString: " + jsonString);
        if (jsonString == null || jsonString.isEmpty()) {
            return false;
        }

        String today = Util.getToday(mDateFormatString);
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String cacheDate = Util.getString(jsonObject, "cacheDate");

            boolean isValid = isValidCacheDate(cacheDate, today);
            //Log.e(mTag, "isValid: " + isValid);

            if (isValid) {
                isValidCache = true;

                JSONArray jsonArray = jsonObject.getJSONArray("items");
                //Log.e(mTag, "jsonArray.toString(): " + jsonArray.toString());
                dataList.clear();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    SiteData data = new SiteData();
                    data.setTitle(Util.getString(object, "title"));
                    data.setDate(Util.getString(object, "date"));
                    data.setContent(Util.getString(object, "content"));
                    data.setUrl(Util.getString(object, "url"));
                    data.setImageUrl(Util.getString(object, "imageUrl"));

                    dataList.add(data);
                }
            }
        } catch (JSONException e) {
            Log.e(mTag, "ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        return isValidCache;
    }
    */

    /**
     * 블로그 캐쉬 저장하기
     */
    /*
    public void saveBlogCache(ArrayList<SiteData> dataList) {
        //Log.e(mTag, "saveBlogCache().........");

        if (dataList == null || dataList.size() == 0) {
            return;
        }

        String today = Util.getToday(mDateFormatString);
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            for (SiteData data : dataList) {
                JSONObject object = new JSONObject();
                object.put("title", data.getTitle());
                object.put("date", data.getDate());
                object.put("content", data.getContent());
                object.put("url", data.getUrl());
                object.put("imageUrl", data.getImageUrl());
                jsonArray.put(object);
            }

            jsonObject.put("cacheDate", today);
            jsonObject.put("items", jsonArray);
            //Log.e(mTag, jsonObject.toString());

            mSharedEditor.putString(mCacheKeyBlog, jsonObject.toString());
            mSharedEditor.commit();
        } catch (JSONException e) {
            Log.e(mTag, "ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    */
    protected boolean isValidCacheDate(String cacheDate, String currentDate) {
        SimpleDateFormat format = new SimpleDateFormat(mDateFormatString, Locale.getDefault());

        try {
            Date d1 = format.parse(cacheDate);
            Date d2 = format.parse(currentDate);

            long diff = d2.getTime() - d1.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;

            return (diffMinutes < Config.CACHE_EXPIRE_TIME); // 15분

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(mTag, e.getMessage());
        }
        return false;
    }
}
