package com.summertaker.akb48guide.parser;

import android.util.Log;

import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.data.MemberData;
import com.summertaker.akb48guide.data.TeamData;
import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class BaseParser {

    protected String mTag;

    public BaseParser() {
        mTag = "===== " + this.getClass().getSimpleName();
    }

    protected String clean(String response) {
        return response;
    }

    public String getUrl(String groupId) {
        return null;
    }

    public void parse48List(String response, GroupData groupData, ArrayList<MemberData> memberList) {

    }

    public void parse46List(String response, GroupData groupData, ArrayList<MemberData> memberList) {

    }

    public void parseMemberList(String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList, boolean isMobile) {
        switch (groupData.getId()) {
            case Config.GROUP_ID_AKB48:
                Akb48Parser akb48Parser = new Akb48Parser();
                akb48Parser.parseMemberList(response, groupData, groupMemberList, teamDataList);
                break;
            case Config.GROUP_ID_SKE48:
                Ske48Parser ske48Parser = new Ske48Parser();
                ske48Parser.parseMemberList(response, groupData, groupMemberList, teamDataList);
                break;
            case Config.GROUP_ID_NMB48:
                Nmb48Parser nmb48Parser = new Nmb48Parser();
                nmb48Parser.parseMemberList(response, groupData, groupMemberList, teamDataList);
                break;
            case Config.GROUP_ID_HKT48:
                Hkt48Parser hkt48Parser = new Hkt48Parser();
                if (isMobile) {
                    hkt48Parser.parseMobileMemberList(response, groupData, groupMemberList, teamDataList);
                } else {
                    hkt48Parser.parseMemberList(response, groupData, groupMemberList, teamDataList);
                }
                break;
            case Config.GROUP_ID_NGT48:
                Ngt48Parser ngt48Parser = new Ngt48Parser();
                if (isMobile) {
                    ngt48Parser.parseMobileMemberList(response, groupData, groupMemberList, teamDataList);
                } else {
                    ngt48Parser.parseMemberList(response, groupData, groupMemberList, teamDataList);
                }
                break;
            case Config.GROUP_ID_JKT48:
                Jkt48Parser jkt48Parser = new Jkt48Parser();
                jkt48Parser.parseMemberList(response, groupData, groupMemberList, teamDataList);
                break;
            case Config.GROUP_ID_SNH48:
                Snh48Parser snh48Parser = new Snh48Parser();
                snh48Parser.parseMemberList(response, groupData, groupMemberList, teamDataList);
                break;
            case Config.GROUP_ID_BEJ48:
                Bej48Parser bej48Parser = new Bej48Parser();
                bej48Parser.parseMemberList(response, groupData, groupMemberList, teamDataList);
                break;
            case Config.GROUP_ID_GNZ48:
                Gnz48Parser gnz48Parser = new Gnz48Parser();
                gnz48Parser.parseMemberList(response, groupData, groupMemberList, teamDataList);
                break;
        }
    }

    public HashMap<String, String> parseProfile(GroupData groupData, String url, String response, boolean isMobile) {
        HashMap<String, String> hashMap = new HashMap<>();

        switch (groupData.getId()) {
            case Config.GROUP_ID_AKB48:
                Akb48Parser akb48Parser = new Akb48Parser();
                hashMap = akb48Parser.parseProfile(response);
                break;
            case Config.GROUP_ID_SKE48:
                Ske48Parser ske48Parser = new Ske48Parser();
                hashMap = ske48Parser.parseProfile(response);
                break;
            case Config.GROUP_ID_NMB48:
                Nmb48Parser nmb48Parser = new Nmb48Parser();
                hashMap = nmb48Parser.parseProfile(url, response);
                break;
            case Config.GROUP_ID_HKT48:
                Hkt48Parser hkt48Parser = new Hkt48Parser();
                if (isMobile) {
                    hashMap = hkt48Parser.parseMobileProfile(response);
                } else {
                    hashMap = hkt48Parser.parseProfile(response);
                }
                break;
            case Config.GROUP_ID_NGT48:
                Ngt48Parser ngt48Parser = new Ngt48Parser();
                if (isMobile) {
                    hashMap = ngt48Parser.parseMobileProfile(response);
                } else {
                    hashMap = ngt48Parser.parseProfile(response);
                }
                break;
            case Config.GROUP_ID_JKT48:
                Jkt48Parser jkt48Parser = new Jkt48Parser();
                hashMap = jkt48Parser.parseProfile(response);
                break;
            case Config.GROUP_ID_SNH48:
                Snh48Parser snh48Parser = new Snh48Parser();
                hashMap = snh48Parser.parseProfile(response);
                break;
            case Config.GROUP_ID_BEJ48:
                Bej48Parser bej48Parser = new Bej48Parser();
                hashMap = bej48Parser.parseProfile(response);
                break;
            case Config.GROUP_ID_GNZ48:
                Gnz48Parser gnz48Parser = new Gnz48Parser();
                hashMap = gnz48Parser.parseProfile(response);
                break;
        }

        return hashMap;
    }


    protected void findTeamMemberOne(ArrayList<MemberData> mGroupMemberList, ArrayList<TeamData> teamDataList) {
        // 팀 목록 만들기
        ArrayList<String> teamNameList = new ArrayList<>();
        String name;
        boolean exist;
        for (int i = 0; i < mGroupMemberList.size(); i++) {
            name = mGroupMemberList.get(i).getTeamName();
            exist = false;
            for (int j = 0; j < teamNameList.size(); j++) {
                if (name.equals(teamNameList.get(j))) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                //Log.e(mTag, teamList.get(i));
                teamNameList.add(name);
            }
        }

        // 팀 대표로 표시할 멤버 한명 찾기
        int memberCount = 0;
        //ArrayList<TeamData> teamDataList = new ArrayList<>();
        ArrayList<MemberData> memberList = new ArrayList<>();
        for (String teamName : teamNameList) {
            memberList.clear();
            memberCount = 0;
            for (MemberData data : mGroupMemberList) {
                if (teamName.equals(data.getTeamName())) {
                    memberList.add(data);
                    memberCount++;
                }
            }

            Collections.shuffle(memberList);
            //Log.e(mTag, tempList.get(0).getTeamName() + " / " + tempList.get(0).getNameJa());
            MemberData member = memberList.get(0);
            //mTeamMemberList.add(member);

            TeamData teamData = new TeamData();
            teamData.setGroupId(member.getGroupId());
            teamData.setGroupName(member.getGroupName());
            teamData.setName(teamName);
            teamData.setMemberCount(memberCount);
            teamData.setMemberData(member);
            teamDataList.add(teamData);
        }
    }

    public void parseAmebaRss(String response, ArrayList<WebData> webDataList) {
        //Log.e(mTag, "parseBlogList()...");
        /*
        <?xml version="1.0" encoding="utf-8" ?>
        <rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
            <channel>
                <title>AKB48 Official Blog 〜1830ｍから～ powered by アメブロ　　</title>
                <link>http://ameblo.jp/akihabara48/</link>
                <atom:link href="http://rssblog.ameba.jp/akihabara48/rss20.xml" rel="self" type="application/rss+xml" />
                <atom:link rel="hub" href="http://pubsubhubbub.appspot.com" />
                <description>
                    ファンのみなさまのおかげで「全国区のアイドルグループとして東京ドームでコンサートを開く」とい...
                </description>
                <language>ja</language>
                <item>
                    <title>抽選及び当選通知配信完了のお知らせ</title>
                    <description>
                        <![CDATA[
                            以下の抽選及び当選通知配信が完了しましたので、お知らせ致します。<br /><br />
                            2016/04/19（火）18:30 「ただいま　恋愛中」公演<br />→ AKB48Mobile会員枠、二本柱の会会員枠<br /><br />
                            2016/04/20（水）18:30 「夢を死なせるわけにいかない」公演<br />→ 100発98中権利、100発100中権利<br /><br />
                            ...
                        ]]>
                    </description>
                    <link>http://ameblo.jp/akihabara48/entry-12058365438.html</link>
                    <pubDate>Sat, 16 Apr 2016 22:04:40 +0900</pubDate>
                </item>
                ...
        */

        response = Util.getJapaneseString(response, "8859_1");
        response = clean(response);
        //Log.e(mTag, "response: " + response);

        Document doc = Jsoup.parse(response);

        for (Element item : doc.select("item")) {
            //Log.e(mTag, item.text());

            String id;
            String title;
            String content;
            String url;
            String date;
            String thumbnailUrl = "";
            String imageUrl = "";

            Element el;

            el = item.select("title").first();
            if (el == null) {
                continue;
            }
            title = el.text().trim();

            el = item.select("description").first();
            if (el == null) {
                continue;
            }

            Document description = Jsoup.parse(el.text());
            content = description.text().trim();
            for (Element img : description.select("img")) {
                String src = img.attr("src");
                if (src.contains(".gif")) {
                    continue;
                }

                thumbnailUrl += img.attr("src") + "*";
                //Log.e(mTag, thumbnailUrl);
                //break;
            }

            // http://stackoverflow.com/questions/15991511/cannot-extract-data-from-an-xml
            url = item.select("link").first().nextSibling().toString().trim();

            // 모바일 페이지 URL로 바로 연결
            url = url.replace("http://ameblo.jp/", "http://s.ameblo.jp/");

            if (url.contains("ske48.co.jp/blog/?id=")) {
                // PC용 URL이 모바일 페이지로 Redirect되면서 파라미터를 잃어버려서 모바일 URL로 바로 연결
                // From: http://www.ske48.co.jp/blog/?id=20151109145725256&writer=secretariat
                // To: http://www2.ske48.co.jp/blog/detail/id:20151109145725256
                String[] array = url.split("id=");
                array = array[1].split("&");
                url = "http://www2.ske48.co.jp/blog/detail/id:" + array[0];
            }

            el = item.select("pubDate").first();
            if (el == null) {
                continue;
            }
            date = el.text().trim();

            id = Util.urlToId(url);

            //Log.e(mTag, title + " / " + url + " / " + thumbnailUrl);

            WebData webData = new WebData();
            webData.setId(id);
            webData.setTitle(title);
            webData.setContent(content);
            webData.setUrl(url);
            webData.setDate(date);
            webData.setThumbnailUrl(thumbnailUrl);

            webDataList.add(webData);
        }
    }

    public void parseAmebaList(String response, ArrayList<WebData> dataList) {
        //Log.e(mTag, "parseBlogList()...");
        /*
        <ul id="entryLi" class="et-list l-std nothumb" data-taplog="blogtop/entry_" data-taplog-loop>
            <li class="list-item skin-bd-color bd-b skinBorderBottomColor">
                <div class="list-main tap-tgt">
                    <a href="http://s.ameblo.jp/ske48official/entry-12183251587.html" data-taplog-i="1/title">
                        <h1 class="et-title">
                            (大矢真那)公演
                            <i class="wf wf-18 wf-r wf-orange wf-amb wf-amb-ak mgl-2"></i>
                        </h1>
                        <div class="et-data c-999 skinWeakColor skin-weak-txt-color">
                            <time class="et-time"> 2016/07/23 0:56:41 </time>
                            <span class="et-iine"><i class="wf wf-12 wf-ambl wf-ambl-g mgr-2"></i>426</span>
                            <span class="et-cmt"><i class="wf wf-12 wf-amb wf-amb-T mgr-2"></i>70</span>
                            <span class="et-reblog"><i amb-icon="reblog hasLabel" class="et-reblog-icon"></i>1</span>
                        </div>
                    </a>
                </div>
            </li>
            ...
        */

        response = clean(response);
        //Log.e(mTag, response.substring(0, 100));

        Document doc = Jsoup.parse(response);
        Element root = doc.getElementById("entryLi");

        for (Element row : root.select("li")) {
            //Log.e(mTag, blog.html());

            String id;
            String title;
            String url;
            String date;

            Element a = row.select("a").first();
            if (a == null) {
                continue;
            }
            url = a.attr("href");

            Element h1 = a.select("h1").first();
            if (h1 == null) {
                continue;
            }
            title = h1.text().trim();

            Element time = a.select(".et-time").first();
            if (time == null) {
                continue;
            }
            date = time.text();

            id = Util.urlToId(url);

            //Log.e(mTag, title + " / " + url + " / " + date);

            WebData webData = new WebData();
            webData.setId(id);
            webData.setTitle(title);
            webData.setUrl(url);
            webData.setDate(date);

            dataList.add(webData);
        }
    }

    public String[] parseAmebaArticle(String response) {
        //Log.e(mTag, "parseBlogList()...");
        /*
        <div class="et-content fs-16" id="js-etContent">
            <div>こんばんは(*^o^*)</div><div><br></div><div><br></div><div>やっふぃーーー(^-^)/</div>
            <a id="i13704132769" class="detailOn" href="http://s.ameblo.jp/ske48official/image-12183239295-13704132769.html">
                <img src="http://stat.ameba.jp/user_images/20160723/00/ske48official/f1/a3/j/o0480036013704132769.jpg" alt="{A87AAA1F-088F-44E0-AFBE-742CBD8471EF}" border="0" height="300" width="400">
            </a>
        */

        response = clean(response);
        //Log.e(mTag, response.substring(0, 100));

        String[] array = new String[2];

        Document doc = Jsoup.parse(response);
        Element root = doc.getElementById("js-etContent");

        if (root == null) {
            return array;
        }

        String content = root.text().trim();
        content = Util.removeSpace(content);
        array[0] = content;

        String imageUrl = "";
        for (Element row : root.select("a")) {
            //Log.e(mTag, blog.html());

            Element img = row.select("img").first();
            if (img == null) {
                continue;
            }
            String src = img.attr("src");
            imageUrl += src + "*";
            //Log.e(mTag, imageUrl);
        }
        array[1] = imageUrl;

        return array;
    }

    public String parseAmebaJson(String response, ArrayList<WebData> webDataList) {
        //Log.e(mTag, "parseAmebaJson()...");
        /*
        Amb.Ameblo.image.Callback({
            "success":true,
            "hasErrors":false,
            "nextUrl":"http://blogimgapi.ameba.jp/image_list/get.jsonp?ameba_id=nmb48&target_ym=201603&limit=20&page=35&sp=false",
            // or "nextUrl":null,
            "imgList":[{
                "imgUrl":"/user_images/20160301/22/nmb48/ad/21/j/o0480064013581406825.jpg",
                "pageUrl":"http://ameblo.jp/nmb48/image-12134614414-13581406825.html",
                "title":"柴田優衣「はじめてのツア…",
                "entryUrl":"http://ameblo.jp/nmb48/entry-12134614414.html"
            },{
                "imgUrl":"/user_images/20160301/22/nmb48/d1/8b/j/o0240018113581390253.jpg",
                "pageUrl":"http://ameblo.jp/nmb48/image-12134607967-13581390253.html",
                "title":"ゆきつん。。teamM",
                "entryUrl":"http://ameblo.jp/nmb48/entry-12134607967.html"
            },
            ...
        */

        response = clean(response);
        response = response.replace("Amb.Ameblo.image.Callback(", "");
        response = response.replace(");", "");
        //Log.e(mTag, response);

        String nextUrl = null;

        try {
            JSONObject jsonObject = new JSONObject(response);
            nextUrl = jsonObject.getString("nextUrl");
            //Log.e(mTag, "nextUrl: " + nextUrl);

            String imgList = jsonObject.getString("nextUrl");
            JSONArray jsonArray = null;
            if (imgList != null && !imgList.equals("null") && !imgList.isEmpty()) {
                jsonArray = jsonObject.getJSONArray("imgList");
            }
            //Log.e(mTag, "jsonArray.toString(): " + jsonArray.toString());

            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    WebData webData = new WebData();

                    webData.setTitle(Util.getString(object, "title"));
                    webData.setUrl(Util.getString(object, "entryUrl"));
                    //webData.setUrl(Util.getString(object, "pageUrl")); // 이미지 페이지

                    String imgUrl = Util.getString(object, "imgUrl");

                    String thumbnailUrl = "http://stat.ameba.jp" + imgUrl;
                    //Log.e(mTag, thumbnailUrl);
                    webData.setThumbnailUrl(thumbnailUrl);

                    String imageUrl = "http://stat001.ameba.jp" + imgUrl;
                    //Log.e(mTag, imageUrl);
                    webData.setImageUrl(imageUrl);

                    webDataList.add(webData);
                    //Log.e(mTag, "- dataList.size(): " + dataList.size());
                }
            }
        } catch (JSONException e) {
            Log.e(mTag, e.getMessage());
        }

        return nextUrl;
    }

    /**
     * 정렬: 날짜 오름차순 (ASC)
     */
    protected static class DateAscCompare implements Comparator<WebData> {
        @Override
        public int compare(WebData arg0, WebData arg1) {
            return arg0.getDate().compareTo(arg1.getDate());
        }
    }

    /**
     * 정렬: 날짜 내림차순 (DESC)
     */
    protected static class DateDescCompare implements Comparator<WebData> {
        @Override
        public int compare(WebData arg0, WebData arg1) {
            return arg1.getDate().compareTo(arg0.getDate());
        }
    }
}
