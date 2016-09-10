package com.summertaker.akb48guide.parser;

import android.content.Context;
import android.util.Log;

import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.data.MemberData;
import com.summertaker.akb48guide.data.SiteData;
import com.summertaker.akb48guide.data.TeamData;
import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.util.Translator;
import com.summertaker.akb48guide.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;

public class Ske48Parser extends BaseParser {

    private String mTag = "##### Ske48Parser";

    /*
    멤버 전체 목록 + 팀 대표로 표시할 멤버 찾기
    */
    public void parseMemberList(Context context, String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList) {
        /*
        <span id="s">
            <h3 class="team">チームS</h3>
        </span>
        <ul class="list clearfix">
            <li>
                <dl>
                    <dt><a href="./?id=rion_azuma"><img src="http://sp.ske48.co.jp/img/120x150/rion_azuma.jpg" alt="東李苑" /></a></dt>
                    <dd>
                        <h3><a href="./?id=rion_azuma">東李苑</a></h3>
                        <h3 class="en">RION AZUMA</h3>
                        <ul class="profBtn">
                            <li><a href="./?id=rion_azuma" class="btn profile"><span></span></a></li>
                            <li><a href="../blog/?writer=rion_azuma" class="btn blog"><span></span></a></li>
                            <li class="textPlus"></li>
                        </ul>
                    </dd>
                </dl>
            </li>
            ...
        </ul>
        */
        response = clean(response);
        response = Util.getJapaneseString(response, "8859_1");

        Translator translator = new Translator(context);

        Document doc = Jsoup.parse(response);

        for (Element row : doc.select("h3.team")) {
            //Log.e(mTag, row.html());

            Element parent = row.parent();
            if (parent == null) {
                continue;
            }

            Element ul = parent.nextElementSibling();
            for (int i = 0; i < 10; i++) {
                if (ul == null) {
                    break;
                }
                //Log.e(mTag, ul.tagName());

                if (!ul.tagName().equals("ul")) {
                    break;
                }

                for (Element li : ul.select("li")) {
                    //Log.e(mTag, li.html());

                    //String id;
                    String teamName = row.text();
                    teamName = translator.translateTeam(groupData.getId(), teamName);

                    String name;
                    String nameEn;
                    String thumbnailUrl;
                    String imageUrl;
                    String profileUrl;

                    Element dl = li.select("dl").first();
                    if (dl == null) {
                        continue;
                    }
                    //Log.e(mTag, dl.html());

                    Element dt = dl.select("dt").first();
                    if (dt == null) {
                        continue;
                    }
                    //Log.e(mTag, dt.html());

                    Element a = dt.select("a").first();
                    if (a == null) {
                        continue;
                    }
                    //Log.e("...", a.attr("href"));

                    // ./?id=rion_azuma
                    profileUrl = a.attr("href");
                    profileUrl = profileUrl.replace("./", "/");
                    profileUrl = "http://www.ske48.co.jp/profile" + profileUrl;

                    Element img = a.select("img").first();
                    if (img == null) {
                        continue;
                    }
                    thumbnailUrl = img.attr("src");
                    imageUrl = thumbnailUrl.replace("120x150", "300x365");

                    Element dd = dl.select("dd").first();
                    if (dd == null) {
                        continue;
                    }

                    Element h3 = dd.select("h3").first();
                    name = h3.text();
                    name = Util.replaceJapaneseWhiteSpace(name);

                    Element h3en = dd.select("h3.en").first();
                    nameEn = h3en.text();
                    nameEn = Util.ucfirstAll(nameEn);

                    //id = Util.urlToId(profileUrl);

                    //Log.e(mTag, teamName + " / " + nameJa + " / " + nameEn + " / " + thumbnailUrl + " / " + profileUrl);

                    MemberData memberData = new MemberData();
                    //memberData.setGroupId(id);
                    memberData.setGroupId(groupData.getId());
                    memberData.setGroupName(groupData.getName());
                    memberData.setTeamName(teamName);
                    memberData.setName(name);
                    memberData.setNameEn(nameEn);
                    memberData.setNoSpaceName(Util.removeSpace(name));
                    memberData.setThumbnailUrl(thumbnailUrl);
                    memberData.setImageUrl(imageUrl);
                    memberData.setProfileUrl(profileUrl);
                    groupMemberList.add(memberData);
                }

                ul = ul.nextElementSibling();
            }
        }

        if (teamDataList != null) {
            super.findTeamMemberOne(groupMemberList, teamDataList);
        }
    }

    public HashMap<String, String> parseProfile(String response) {
        /*
        <div class="detail">
            <dl class="profile clearfix">
                <dt>
                    <!-- PHOTO -->
                    <img src="http://sp.ske48.co.jp/img/300x365/ryoha_kitagawa.jpg" alt="北川綾巴" oncontextmenu="return false" />
                    <!-- /PHOTO -->
                </dt>
                <dd>
                    <h3>北川綾巴</h3>
                    <h3 class="en">RYOHA KITAGAWA</h3>
                    SKE48チームS / AKB48チーム4兼任<!-- DATA -->
                    <!-- DATA -->
                    <ul>
                        <li>ニックネーム：うは</li>
                        <li>6期生</li>
                        <li>生年月日：1998年10月9日</li>
                        <li>血液型：B型</li>
                        <li>出身地：愛知県</li>
                        <li>身長：160cm</li>
                        <li>キャッチフレーズ：あなたに必要なざい"りょうは?"(りょうは)<br />あなたに必要とされたいな<br />ちょっぴりツンデレ北川綾巴です。</li>
                        <li>趣味：寝ること、食べること、メンバーと喋ること。</li>
                        <li>特技：どこでも寝られること</li>
                        <li>将来の夢：たくさんの人を笑顔にすること。</li>
                        <li>好きな食べ物：アイス、フルーツ</li>
                        <li>好きな言葉：自分の夢にだけは素直でいたいんだ！</li>
                        <li>一言メッセージ：これからもっと努力して成長していきますので応援よろしくお願いします。<br /></li>
                   </ul>
        */
        response = clean(response);
        response = Util.getJapaneseString(response, "8859_1");
        //Log.i("##### response", response);

        HashMap<String, String> hashMap = new HashMap<>();

        Document doc = Jsoup.parse(response); // http://jsoup.org/

        Element root = doc.select(".detail").first();
        if (root == null) {
            return hashMap;
        }

        Element profile = root.select(".profile").first();
        if (profile == null) {
            return hashMap;
        }

        String imageUrl = "";
        Element dl = profile.select("dl").first();
        if (dl == null) {
            return hashMap;
        }
        Element img = dl.select("img").first();
        if (img == null) {
            return hashMap;
        }
        imageUrl = img.attr("src").trim();
        hashMap.put("imageUrl", imageUrl);

        Element dd = profile.select("dd").first();
        if (dd == null) {
            return hashMap;
        }
        Element ja = dd.select("h3").first();
        if (ja == null) {
            return hashMap;
        }
        String name = ja.text().trim();
        hashMap.put("name", name);

        Element en = dd.select(".en").first();
        if (en == null) {
            return hashMap;
        }
        String nameEn = en.text().trim();
        hashMap.put("nameEn", nameEn);

        //Log.e(mTag, nameJa + " / " + nameEn);

        Element ul = profile.select("ul").first();
        if (ul == null) {
            return hashMap;
        }
        String html = "";
        for (Element li : ul.select("li")) {
            String text = li.text().trim();
            String[] array = text.split("：");
            if (array.length == 2) {
                String title = array[0].trim();
                String value = array[1].trim();
                html += title + ": " + value;
            } else {
                html += text;
            }
            html += "<br>";
        }
        html = (html + "<br>").replace("<br><br>", "");

        hashMap.put("html", html);

        hashMap.put("isOk", "ok");

        return hashMap;
    }

    public String parseBlogSiteList(String response, ArrayList<SiteData> dataList) {
        /*
        <ul class="list clearfix">
            <li class="post01">
                <dl>
                    <dt>
                        <a href="./?writer=rion_azuma">
                            <img src="http://sp.ske48.co.jp/img/100x80/rion_azuma.jpg" alt="東李苑 東李苑オフィシャルブログ" />
                        </a>
                    </dt>
                    <dd>
                        <h3><a href="./?writer=rion_azuma">東李苑</a></h3>
                        <h3 class="en">2016.07.18 20:35</h3>
                    </dd>
                </dl>
            </li>
        */
        response = clean(response);
        response = Util.getJapaneseString(response, "8859_1");
        //Log.e(mTag, response);

        Document doc = Jsoup.parse(response);

        for (Element ul : doc.select(".list")) {

            String text = ul.previousElementSibling().text();
            //Log.e(mTag, text);
            if (text.contains("アーカイブ")) {
                continue;
            }

            for (Element li : ul.select("li")) {

                Element a = li.select("a").first();
                if (a == null) {
                    continue;
                }
                String href = a.attr("href").trim();
                String[] array = href.split("writer=");
                String id = array[1];

                // http://www.ske48.co.jp/blog/?writer=kimoto_kanon
                String url = href.replace("./", "/");
                url = "http://www.ske48.co.jp/blog" + url;

                Element img = a.select("img").first();
                String imageUrl = img.attr("src").trim();

                if (id.equals("secretariat")) {
                    imageUrl = "http://web-m.webcdn.stream.ne.jp/www09/web-m/ske48/img/blog_header/large/secretariat.jpg";
                } else if (id.equals("kenkyuuseiall")) {
                    imageUrl = "http://sp.ske48.co.jp/img/660x250/kenkyuuseiall.jpg";
                } else {
                    // http://sp.ske48.co.jp/img/120x150/rion_azuma.jpg // 프로필 이미지
                    imageUrl = "http://sp.ske48.co.jp/img/120x150/" + id + ".jpg";
                }

                Element dd = li.select("dd").first();
                Element el = dd.select("a").first();
                String name = el.text().trim();

                el = dd.select(".en").first();
                String updateDate = el.text();

                //Log.e(mTag, name + " / " + updateDate);

                SiteData data = new SiteData();
                data.setId(Config.BLOG_ID_SKE48_MEMBER);
                data.setGroupId(Config.GROUP_ID_SKE48);
                data.setName(name);
                data.setLocaleName(name);
                data.setUrl(url);
                data.setImageUrl(imageUrl);
                data.setUpdateDate(updateDate);
                data.setUpdated(false);

                dataList.add(data);
            }
        }

        Element script = doc.select("script").last();
        //Log.e(mTag, "script: " + script.text());
        String text = "";
        for (DataNode node : script.dataNodes()) {
            text = node.getWholeData();
        }
        text = text.replace("var blogUpdate = ", "");
        //Log.e(mTag, text);
        return text;
    }

    public void parseMobileBlogSiteList(String response, ArrayList<SiteData> dataList) {
        /*
        <ul class="team_list clearfix">
            <li>
                <a href="/blog/detail/id:20160718193505660">
                    <span class="photo_guard post24"></span>
                    <img src="http://sp.ske48.co.jp/img/400x400/rion_azuma.jpg" alt="東李苑"/>
                    東李苑
                    <span class="hourtime">2016.07.18 20:35</span>
                </a>
            </li>
        */
        response = clean(response);
        response = Util.getJapaneseString(response, "8859_1");
        //Log.e(mTag, response);

        Document doc = Jsoup.parse(response);

        for (Element ul : doc.select(".team_list")) {

            /*String text = ul.previousElementSibling().text();
            //Log.e(mTag, text);
            if (text.contains("アーカイブ")) {
                continue;
            }*/

            for (Element li : ul.select("li")) {

                Element a = li.select("a").first();
                if (a == null) {
                    continue;
                }
                String href = a.attr("href").trim();
                String url = "http://www2.ske48.co.jp" + href;

                Element img = a.select("img").first();
                String imageUrl = img.attr("src").trim();

                String name = img.attr("alt").trim();

                Element hourtime = a.select(".hourtime").first();
                String updateDate = hourtime.text() + ":00";

                //Log.e(mTag, name + " / " + updateDate + " / " + imageUrl + " / " + url);

                SiteData data = new SiteData();
                data.setId(imageUrl);
                data.setBlogId(Config.BLOG_ID_SKE48_MEMBER);
                data.setGroupId(Config.GROUP_ID_SKE48);
                data.setName(name);
                data.setLocaleName(name);
                data.setUrl(url);
                data.setImageUrl(imageUrl);
                data.setUpdateDate(updateDate);
                data.setUpdated(false);

                dataList.add(data);
            }
        }
    }

    public void parseMobileBlogArticleList(String response, WebData webData) {
        /*
        <div id="blog_detail">
			<h3>ri(・o・`)n 至福だ </h3>
			<time>2016.07.19 23:51</time>
			<div class="blogText">
				<div style="text-align:center;">
				    <img src="http://img.ske48.co.jp/blog2/rion_azuma/146893628901325.jpg" border="0" />
				</div>
				<br />こんばんは<br />東 李苑です！<br /><br />昨日は寝落ちしてしまいアメブロさん更新できずすみません…<br /><br />
				昨日は久しぶりに新幹線でも爆睡でして笑<br />お家帰って、おふろあがって、速攻で寝てしまいました…<br /><br /><br />
				今日は1日おやすみだったので<br />いっぱい寝て、溜まっていたアニメとドラマを見て過ごせました(* ´ ω ` *)<br /><br />
				至福だーーーっと感じながら見てました♪<br /><br /><br />今期は面白いドラマが多いです。<br />好きな人がいること<br />
				時をかける少女<br />仰げば尊し<br /><br />名古屋ではやってないけど<br />死幣ももちろん、北海道で録画でして見ます。
				<br /><br />皆さんは何を見ていますか？<br /><br /><br />ri(・o・`)n＊東 李苑
		    </div><!-- blogText -->
		</div><!-- blog_detail -->
        */
        response = clean(response);
        response = Util.getJapaneseString(response, "8859_1");
        //Log.e(mTag, response);

        Document doc = Jsoup.parse(response);

        Element root = doc.getElementById("blog_detail");

        Element el = root.select("h3").first();
        if (el == null) {
            return;
        }
        String title = root.text().trim();
        //Log.e(mTag, title);

        el = root.select("time").first();
        if (el == null) {
            return;
        }
        String date = el.text().trim();

        el = root.select(".blogText").first();
        if (el == null) {
            return;
        }
        String content = el.text();
        content = Util.removeSpace(content);

        String imageUrl = "";
        for (Element img : el.select("img")) {
            imageUrl += img.attr("src") + "*";
        }

        //Log.e(mTag, name + " / " + updateDate + " / " + imageUrl + " / " + url);

        webData.setTitle(title);
        webData.setDate(date);
        webData.setImageUrl(imageUrl);
        webData.setContent(content);
    }
}
