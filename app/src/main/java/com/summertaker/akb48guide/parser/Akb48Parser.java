package com.summertaker.akb48guide.parser;

import android.util.Log;

import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.data.MemberData;
import com.summertaker.akb48guide.data.TeamData;
import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;

public class Akb48Parser extends BaseParser {

    public void parseMemberList(String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList) {
        /*
        <div class="memberList">
            <ul class="memberListUl">
                <li>
                    <a href="detail.php?mid=51" style="display:block; position:relative; width:170px; height:170px;">
                        <img src="//cdn.akb48.co.jp/cache/image/?path=%2Fmembers%2Fprofile20150511%2Fteam_A_png%2Firiyama_anna.png" width="170" height="170" alt="" style="position:absolute; top:0; left:0;" />
                        <img src="//cdn.akb48.co.jp/common/img/about/member/member/copy_erace.png" width="163" height="170" alt="" style="position:absolute; top:0; left:0;" />
                    </a>
                    <div class="memberListProfile">
                        <h4 class="memberListNamej">入山 杏奈</h4>
                        <p class="memberListNamee">Anna Iriyama</p>
                        <h5 class="memberListBirthDay"><img src="//cdn.akb48.co.jp/common/img/about/member/icon_birthday.gif" width="16" height="12" alt="誕生日" />1995.12.03</h5>
                        <h5 class="memberListTeam">AKB48 Team A  /<br />HKT48 Team K IV</h5>
                    </div>
                </li>
                ...
            </ul>
        </div>
        */
        if (response == null || response.isEmpty()) {
            return;
        }
        response = clean(response);
        Document doc = Jsoup.parse(response);

        Element root = doc.select(".memberListUl").first();
        if (root != null) {
            for (Element row : root.select("li")) {
                String id;
                String teamName;
                String name;
                String nameEn;
                String noSpaceName;
                String thumbnailUrl;
                String profileUrl;

                Element el;

                el = row.select("a").first();
                profileUrl = el.attr("href");
                profileUrl = "http://www.akb48.co.jp/about/members/" + profileUrl;

                el = row.select("img").first();
                if (el == null) {
                    continue;
                }
                //http://cdn.akb48.co.jp/cache/image/?path=%2Fmembers%2Fprofile20150511%2Fteam_B_png%2Fogasawara_mayu.png
                thumbnailUrl = "http:" + el.attr("src");
                thumbnailUrl = thumbnailUrl.replace("%0D%0A", "").replace("\r", "").replace("\n", "");
                //Log.e(mTag, thumbnailUrl);

                el = row.select(".memberListNamej").first();
                if (el == null) {
                    continue;
                }
                name = el.text().trim();
                noSpaceName = Util.removeSpace(name);

                el = row.select(".memberListNamee").first();
                if (el == null) {
                    continue;
                }
                nameEn = el.text().trim();

                el = row.select(".memberListTeam").first();
                if (el == null) {
                    continue;
                }
                teamName = el.text();
                if (teamName.contains("/")) {
                    String[] array = teamName.split("/");
                    teamName = array[0].trim();
                    //if (array.length > 1) {
                    //    teamName2 = array[1];
                    //}
                }
                teamName = teamName.replace("AKB48", "").trim();

                id = Util.urlToId(profileUrl);

                //Log.e(mTag, id + " / " + teamName + " / " + nameJa + " / " + nameEn + " / " + thumbnailUrl + " / " + profileUrl);

                MemberData memberData = new MemberData();
                memberData.setId(id);
                memberData.setGroupId(groupData.getId());
                memberData.setGroupName(groupData.getName());
                memberData.setTeamName(teamName);
                memberData.setName(name);
                memberData.setNameEn(nameEn);
                memberData.setNoSpaceName(noSpaceName);
                memberData.setThumbnailUrl(thumbnailUrl);
                memberData.setImageUrl(thumbnailUrl);
                memberData.setProfileUrl(profileUrl);
                groupMemberList.add(memberData);
            }
        }

        if (teamDataList != null) {
            super.findTeamMemberOne(groupMemberList, teamDataList);
        }
    }

    public void parseMobileTeamList(String response, ArrayList<WebData> webDatas) {
        /*
        <section>
            <h2>チーム</h2>
            <div class="unLine"></div>
            <ul class="infoList">
                <li><a data-ajax="false" href="./member/index.php?g_code=83100476"><p>Team A</p></a></li>
                <li><a data-ajax="false" href="./member/index.php?g_code=83100477"><p>Team K</p></a></li>
                <li><a data-ajax="false" href="./member/index.php?g_code=83100478"><p>Team B</p></a></li>
                <li><a data-ajax="false" href="./member/index.php?g_code=83100606"><p>Team 4</p></a></li>
                <li><a data-ajax="false" href="./member/index.php?g_code=83100803"><p>Team 8</p></a></li>
            </ul>
        </section>
        */
        if (response == null || response.isEmpty()) {
            return;
        }
        response = clean(response);

        Document doc = Jsoup.parse(response);
        Element root = null;

        for (Element section : doc.select("section")) {
            Element h2 = section.select("h2").first();
            if (h2.text().equals("チーム")) {
                root = section.select("ul").first();
            }
        }

        if (root == null) {
            return;
        }

        for (Element li : root.select("li")) {
            String name;
            String url;

            Element a = li.select("a").first();

            name = a.text();

            url = a.attr("href");
            url = "http://sp.akb48.co.jp/profile/" + url;

            Log.e(mTag, name + ", " + url);

            WebData webData = new WebData();
            webData.setName(name);
            webData.setUrl(url);
            webDatas.add(webData);
        }
    }

    public void parseMobileMemberList(String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList) {
        /*
        <ul class="infoList">
        <li>
        <a href="./detail/index.php?artist_code=83100536&g_code=all" data-ajax="false">
          <div class="textCenterBox">
            <div class="photo"><img class="lazy borderPink" data-original="http://image.excite.co.jp/jp/akb48/image/smartphone/20160509/profile/thumb/83100536.jpg" alt="入山杏奈"></div>
            <div class="text02">
              <p class="textbBld pnk fL">入山杏奈</p>
              <p class="lineRight colorPink02 r fR f12">Anna Iriyama</p>
            </div>
          </div>
        </a>
        </li>
        */
        if (response == null || response.isEmpty()) {
            return;
        }
        response = clean(response);

        Document doc = Jsoup.parse(response);
        Element root = doc.select(".infoList").first();

        if (teamDataList != null) {
            super.findTeamMemberOne(groupMemberList, teamDataList);
        }
    }

    public void parseMobileMemberListOfAll(String response, GroupData groupData, ArrayList<MemberData> groupMemberList) {
        /*
        <ul class="infoList">
        <li>
        <a href="./detail/index.php?artist_code=83100536&g_code=all" data-ajax="false">
          <div class="textCenterBox">
            <div class="photo"><img class="lazy borderPink" data-original="http://image.excite.co.jp/jp/akb48/image/smartphone/20160509/profile/thumb/83100536.jpg" alt="入山杏奈"></div>
            <div class="text02">
              <p class="textbBld pnk fL">入山杏奈</p>
              <p class="lineRight colorPink02 r fR f12">Anna Iriyama</p>
            </div>
          </div>
        </a>
        </li>
        */
        if (response == null || response.isEmpty()) {
            return;
        }
        response = clean(response);

        Document doc = Jsoup.parse(response);
        Element root = doc.select(".infoList").first();

        if (root != null) {
            for (Element row : root.select("li")) {
                String name;
                String nameEn;
                String noSpaceName;
                String thumbnailUrl;
                String profileUrl;

                Element el;

                Element a = row.select("a").first();
                profileUrl = a.attr("href");
                profileUrl = profileUrl.replace("./", "/");
                profileUrl = "http://sp.akb48.co.jp/profile/member" + profileUrl;

                Element img = a.select("img").first();
                if (img == null) {
                    continue;
                }
                thumbnailUrl = img.attr("data-original");
                //Log.e(mTag, thumbnailUrl);

                el = a.select(".textbBld").first();
                if (el == null) {
                    continue;
                }
                name = el.text().trim();

                // 모바일용 사이트 이름과 PC용 사이트 이름이 다르다. PC용으로 전환
                switch (name) {
                    case "浜咲友菜":
                        name = "濵咲友菜";
                        break;
                }

                noSpaceName = Util.removeSpace(name);

                el = a.select(".lineRight").first();
                if (el == null) {
                    continue;
                }
                nameEn = el.text().trim();

                //Log.e(mTag, name + " / " + nameEn + " / " + thumbnailUrl + " / " + profileUrl);

                MemberData memberData = new MemberData();
                memberData.setGroupId(groupData.getId());
                memberData.setGroupName(groupData.getName());
                memberData.setName(name);
                memberData.setNameEn(nameEn);
                memberData.setNoSpaceName(noSpaceName);
                memberData.setThumbnailUrl(thumbnailUrl);
                memberData.setImageUrl(thumbnailUrl);
                memberData.setProfileUrl(profileUrl);
                groupMemberList.add(memberData);
            }
        }
    }

    public HashMap<String, String> parseProfile(String response) {
        /*
        <div class="memberDetail">
            <div class="memberDetailPhoto">
                <img src="//cdn.akb48.co.jp/cache/image/?path=%2Fmembers%2Fprofile20150511%2Fteam_A_png%2Firiyama_anna.png" width="170" height="170" alt="入山 杏奈" />
            </div>
            <div class="memberDetailProfile">
                <p class="memberDetailProfileHurigana">イリヤマ アンナ</p>
                <h3 class="memberDetailProfileName">入山 杏奈</h3>
                <p class="memberDetailProfileEName">Anna Iriyama</p>

                <div class="memberDetailProfileWrapper">
                    <ul>
                        <li>
                            <h4 class="memberDetailProfileLeft">Office</h4>
                            <p class="memberDetailProfileRight">太田プロダクション</p>
                        </li>
                        <li>
                            <h4 class="memberDetailProfileLeft">Nickname</h4>
                            <p class="memberDetailProfileRight">あんにん</p>
                        </li>
                        <li>
                            <h4 class="memberDetailProfileLeft">Date of birth</h4>
                            <p class="memberDetailProfileRight">1995.12.03</p>
                        </li>
                        <li>
                            <h4 class="memberDetailProfileLeft">From</h4>
                            <p class="memberDetailProfileRight">Chiba</p>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
        */
        response = clean(response);
        //Log.i("##### response", response);

        HashMap<String, String> hashMap = new HashMap<>();

        Document doc = Jsoup.parse(response); // http://jsoup.org/

        Element root = doc.select(".memberDetail").first();
        if (root == null) {
            return hashMap;
        }

        String imageUrl = "";
        Element photo = root.select(".memberDetailPhoto > img").first();
        if (photo == null) {
            return hashMap;
        }
        imageUrl = "http:" + photo.attr("src");
        hashMap.put("imageUrl", imageUrl);

        Element profile = doc.select(".memberDetailProfile").first();
        if (profile == null) {
            return hashMap;
        }
        //Log.i("##### root", root.toString());

        Element el;

        el = profile.select(".memberDetailProfileHurigana").first();
        if (el == null) {
            return hashMap;
        }
        hashMap.put("furigana", el.text().trim());

        el = profile.select(".memberDetailProfileName").first();
        if (el == null) {
            return hashMap;
        }
        String name = el.text().trim();
        name = Util.replaceJapaneseWhiteSpace(name);
        hashMap.put("name", name);

        el = profile.select(".memberDetailProfileEName").first();
        if (el == null) {
            return hashMap;
        }
        String nameEn = el.text().trim();
        hashMap.put("nameEn", nameEn);

        //Log.e(mTag, nameJa + " / " + nameEn);

        String html = "";
        Element subdetail = profile.select(".memberDetailProfileWrapper").first();
        if (subdetail != null) {
            Element ul = subdetail.select("ul").first();
            if (ul != null) {
                int count = 0;
                for (Element li : ul.select("li")) {
                    String title = li.child(0).text().trim();
                    String value = li.child(1).text().trim();
                    if (count > 0) {
                        html += "<br>";
                    }
                    html += title + "：" + value;
                    count++;
                }
            }
        }
        hashMap.put("html", html);

        hashMap.put("isOk", "ok");

        return hashMap;
    }

    public void parseTeam8ReportList(String response, ArrayList<WebData> webDataList) {
        /*
        <div class="thumbnailList clearfix">
            <div class="thumbnailBox">
                <a href="/report/list/20160427-421694791758971.php" class="cntInner">
                    <div class="thumbPhoto photoFixLayout"><img src="/report/assets/2016/04/thumb-9402_118-118.jpg" height="118" alt="4月24日(日)放送の「ミライ☆モンスター」に香川県代表の行天優莉奈が出演しました。"></div>
                    <p class="date">2016.04.27</p>
                    <p class="category">
                        <img src="/module/img/common/category_icon_media.png" height="20" width="60" alt=". media .">
                    </p>
                    <p class="mainTitle trunk8Box">4月24日(日)放送の「ミライ☆モンスター」に香川県代表の行天優莉奈が出演しました。</p>
                </a>
            </div>
        */
        response = clean(response);
        //response = Util.getJapaneseString(response, "SHIFT-JIS");
        //Log.e(mTag, response);

        Document doc = Jsoup.parse(response);
        Element root = doc.select(".thumbnailList").first();

        for (Element row : root.select(".thumbnailBox")) {
            String id;
            String title = null;
            String date = null;
            String content;
            String url;
            String thumbnailUrl = null;
            String imageUrl = null;

            Element el;

            Element a = row.select("a").first();
            if (a == null) {
                continue;
            }
            url = "http://toyota-team8.jp" + a.attr("href");
            url = url.replace("/report/", "/report/sp/"); // go to Mobile Site.

            el = a.select(".thumbPhoto").first();
            if (el != null) {
                el = el.select("img").first();
                thumbnailUrl = "http://toyota-team8.jp" + el.attr("src");
            }

            el = a.select(".date").first();
            if (el != null) {
                date = el.text().trim();
            }

            el = a.select(".mainTitle").first();
            if (el != null) {
                title = el.text().trim();
            }

            id = Util.urlToId(url);

            //Log.e(mTag, title + " / " + url + " / " + thumbnailUrl + " / " + imageUrl);

            //if (!exist) {
            WebData webData = new WebData();
            webData.setId(id);
            webData.setTitle(title);
            webData.setDate(date);
            webData.setUrl(url);
            webData.setThumbnailUrl(thumbnailUrl);

            webDataList.add(webData);
        }
    }
}
