package com.summertaker.akb48guide.parser;

import com.summertaker.akb48guide.common.Config;
import com.summertaker.akb48guide.data.GroupData;
import com.summertaker.akb48guide.data.MemberData;
import com.summertaker.akb48guide.data.TeamData;
import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

public class Nogizaka46Parser extends BaseParser {

    public void parseMemberList(String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList) {
        //Log.d(mTag, "response: " + response);
        response = clean(response);

        Document doc = Jsoup.parse(response);
        //Log.d(mTag, "doc: " + doc.toString());

        /*
        <div class="unit">
            <a href="./detail/akimotomanatsu.php">
                <img src="http://img.nogizaka46.com/www/img/dot.gif" class="akimotomanatsu" width="124" height="150" alt="秋元 真夏" />
                <span class="main">秋元 真夏</span>
                <span class="sub">あきもと まなつ</span>
                <!-- sub2 -->
            </a>
        </div>
        */

        for (Element a : doc.select("div.unit > a")) {
            //Log.e(mTag, a.html());

            String id;
            String name;
            String furigana;
            String profileUrl;
            String thumbnailUrl;
            String imageUrl;

            Element el = null;

            String href = a.attr("href").replaceFirst("./", "/").trim();

            // http://www.nogizaka46.com/member/detail/etoumisa.php
            profileUrl = "http://www.nogizaka46.com/member" + href;

            String[] array = profileUrl.split("/detail/");
            String text = array[1].replace(".php", "").trim();
            //id = "nogizaka46-" + text;

            // http://img.nogizaka46.com/www/smph/member/img/ikutaerika_prof.jpg
            // http://img.nogizaka46.com/www/member/img/etoumisa_prof.jpg
            thumbnailUrl = "http://img.nogizaka46.com/www/smph/member/img/" + text + "_prof.jpg";
            imageUrl = thumbnailUrl;

            el = a.select(".main").first();
            if (el == null) {
                continue;
            }
            name = el.text().trim();

            el = a.select(".sub").first();
            if (el == null) {
                continue;
            }
            furigana = el.text().trim();

            id = Util.urlToId(profileUrl);

            //Log.e(mTag, nameJa + " / " + furigana + " / " + profileUrl + " / " + thumbnailUrl);

            MemberData memberData = new MemberData();
            memberData.setId(id);
            memberData.setGroupId(Config.GROUP_ID_NOGIZAKA46);
            memberData.setName(name);
            memberData.setFurigana(furigana);
            memberData.setNoSpaceName(Util.removeSpace(name));
            memberData.setProfileUrl(profileUrl);
            memberData.setThumbnailUrl(thumbnailUrl);
            memberData.setImageUrl(imageUrl);

            groupMemberList.add(memberData);
        }
    }

    public HashMap<String, String> parseProfile(String response) {

        HashMap<String, String> hashMap = new HashMap<>();

        /*
        <div id="profile" class="clearfix">
            <img src="http://img.nogizaka46.com/www/member/img/etoumisa_prof.jpg" width="290" height="350" />
            <div class="txt">
                <h2><span>えとう みさ </span>衛藤 美彩</h2>
                <dl>
                    <dt>生年月日：</dt><dd>1993年1月4日</dd>
                    <dt>血液型：</dt><dd>AB型</dd>
                    <dt>星座：</dt><dd>やぎ座</dd>
                    <dt>身長：</dt><dd>162cm</dd>
                </dl>
                <div class="status">
                    <div>1期生</div>
                    <div>選抜メンバー</div>
                    <div>十福神</div>
                </div>
            </div>
        </div>
        */
        response = clean(response);
        //Log.e(mTag, response);

        Document doc = Jsoup.parse(response);

        Element root = doc.getElementById("profile");
        if (root == null) {
            return hashMap;
        }
        //Log.e(mTag, root.html());

        String imageUrl = "";
        Element img = root.select("img").first();
        if (img == null) {
            return hashMap;
        }

        imageUrl = img.attr("src").trim();
        hashMap.put("imageUrl", imageUrl);

        Element txt = root.select(".txt").first();
        if (txt == null) {
            return hashMap;
        }
        Element h2 = txt.select("h2").first();
        if (h2 == null) {
            return hashMap;
        }

        Element span = txt.select("span").first();
        if (span == null) {
            return hashMap;
        }
        String furigana = span.text().trim();
        hashMap.put("furigana", furigana);

        String name = h2.text();
        name = name.replace(furigana, "").trim();
        hashMap.put("name", name);
        //Log.e(mTag, "name: " + name);

        Element dl = txt.select("dl").first();
        if (dl == null) {
            return hashMap;
        }
        //Log.e(mTag, dl.html());

        String html = "";
        for (Element dt : dl.select("dt")) {
            String title = dt.text();
            //Log.e(mTag, title);
            Element dd = dt.nextElementSibling();
            if (dd != null) {
                String value = dd.text().trim();
                html += title + value;
            } else {
                html += title;
            }
            html += "<br>";
        }

        Element status = txt.select(".status").first();
        if (status != null) {
            for (Element div : status.select("div")) {
                html += div.text().trim() + "<br>";
            }
        }

        //if (!html.isEmpty()) {
        //    html = (html + "<br>").replace("<br><br>", "");
        //}
        hashMap.put("html", html);

        Element blog = doc.getElementById("blogmodule");
        if (blog != null) {
            Element a = blog.select(".more").first().select("a").first();
            if (a != null) {
                hashMap.put(Config.SITE_ID_BLOG, a.attr("href").trim());
            }
        }

        hashMap.put("isOk", "ok");

        return hashMap;
    }

    public void parseMobileMemberList(String response, GroupData groupData, ArrayList<MemberData> groupMemberList, ArrayList<TeamData> teamDataList) {
        response = clean(response);
        //response = Util.getJapaneseString(response, "SHIFT-JIS");
        //Log.e(mTag, response);

        Document doc = Jsoup.parse(response);
        //Log.e(mTag, doc.html());

        /*
        <div id="member">
            <ul class="clearfix">
                <li>
                    <a href="./detail/akimotomanatsu.php">
                        <img src="/smph/member/img/akimotomanatsu_list.jpg?v2" alt="秋元 真夏" />
                        <span class="heading">秋元 真夏</span>
                        <span class="sub">あきもと まなつ</span>
                        <!-- sub2 -->
                    </a>
                </li>
                ...
    	    </ul>
    	</div>
        */

        Element root = doc.select("#member > ul").first();
        if (root == null) {
            return;
        }
        //Log.e(mTag, root.html());

        for (Element row : root.select("li > a")) {
            //Log.e(mTag, row.html());

            String id;
            String name;
            String furigana;
            String profileUrl;
            String thumbnailUrl;
            String imageUrl;

            Element el;

            // http://www.nogizaka46.com/smph/member/detail/akimotomanatsu.php
            // ./detail/akimotomanatsu.php
            profileUrl = row.attr("href").replace("./", "/");
            profileUrl = "http://www.nogizaka46.com/smph/member" + profileUrl;

            // http://www.nogizaka46.com/smph/member/img/itoumarika_list.jpg?v2
            // /smph/member/img/akimotomanatsu_list.jpg?v2
            thumbnailUrl = row.select("img").first().attr("src");
            thumbnailUrl = thumbnailUrl.replace("_list", "_prof");
            thumbnailUrl = "http://www.nogizaka46.com" + thumbnailUrl;
            imageUrl = thumbnailUrl;

            name = row.select("span.heading").first().text();

            furigana = row.select("span.sub").first().text();

            id = Util.urlToId(profileUrl);

            //Log.e(mTag, nameJa + " / " + furigana + " / " + profileUrl + " / " + thumbnailUrl);

            MemberData memberData = new MemberData();
            memberData.setId(id);
            memberData.setGroupId(groupData.getId());
            memberData.setGroupName(groupData.getName());
            memberData.setName(name);
            memberData.setFurigana(furigana);
            memberData.setNoSpaceName(Util.removeSpace(name));
            memberData.setProfileUrl(profileUrl);
            memberData.setThumbnailUrl(thumbnailUrl);
            memberData.setImageUrl(imageUrl);

            groupMemberList.add(memberData);
        }
    }

    public HashMap<String, String> parseMobileProfile(String response) {
        HashMap<String, String> hashMap = new HashMap<>();

        response = clean(response);
        //Log.e(mTag, response);

        /*
        <div id="member">
            <div class="pic"><img src="http://img.nogizaka46.com/www/smph/member/img/etoumisa_prof.jpg?v201303" width="145" alt="衛藤 美彩" /></div>
            <h3>衛藤 美彩<span class="sub">（えとう みさ）</span></h3>
            <dl class="clearfix">
                <dt>生年月日</dt><dd>1993年1月4日</dd>
                <dt>血液型</dt><dd>AB型</dd>
                <dt>星座</dt><dd>やぎ座</dd>
                <dt>身長</dt><dd>162cm</dd>
            </dl>
            <div class="status">
                <div>1期生</div>
                <div>選抜メンバー</div>
            </div>
        </div>

        <div id="menu2" class="clearfix">
            <a href="http://www.nogizaka46.com/smph/">TOPページ</a>
            <a href="http://www.nogizaka46.com/smph/news/">ニュース</a>
            ...
            <select onchange="javascript:if(this.value!='')location.href=this.value;">
            <option value="">ﾒﾝﾊﾞｰﾌﾞﾛｸﾞ</option>
            <option value="http://blog.nogizaka46.com/smph/">ブログTOP</option>
            <option value="">- - - - -</option>
            <option value="http://blog.nogizaka46.com/manatsu.akimoto/smph/">秋元 真夏</option>
            <option value="http://blog.nogizaka46.com/erika.ikuta/smph/">生田 絵梨花</option>
            ...
            </select>
        */

        Document doc = Jsoup.parse(response);
        Element root = doc.getElementById("member");
        if (root == null) {
            return hashMap;

        }

        Element el;
        el = root.select(".pic").first();
        if (el == null) {
            return hashMap;
        }
        el = el.select("img").first();
        String imageUrl = el.attr("src").trim();
        hashMap.put("imageUrl", imageUrl);

        Element h3 = root.select("h3").first();
        if (h3 == null) {
            return hashMap;
        }
        String[] array = h3.html().split("<span ");
        if (array.length != 2) {
            return hashMap;
        }
        String name = array[0];
        hashMap.put("name", name);

        el = h3.select(".sub").first();
        String furigana = el.text();
        furigana = furigana.replace("（", "").replace("）", "");
        hashMap.put("furigana", furigana);

        //Log.e(mTag, nameJa + " / " + furigana);

        String html = "";
        for (Element dt : root.select("dt")) {
            String title = dt.text();
            Element dd = dt.nextElementSibling();
            if (dd != null) {
                String value = dd.text().trim();
                html += title + "：" + value;
            } else {
                html += title;
            }
            html += "<br>";
        }

        //ArrayList<String> statusList = new ArrayList<>();
        Element status = root.select(".status").first();
        if (status == null) {
            return hashMap;
        }
        int count = 0;
        for (Element row : status.select("div")) {
            //statusList.add(row.text().trim());
            if (count > 0) {
                html += "<br>";
            }
            html += row.text().trim();
            //Log.e(mTag, row.text());
            count++;
        }
        //Log.e(mTag, statusList.toString());

        //if (!html.isEmpty()) {
        //    html = (html + "<br>").replace("<br><br>", "");
        //}
        hashMap.put("html", html);

        /*Element blog = doc.getElementById("blogmodule");
        if (blog != null) {
            Element a = blog.select(".more").first().select("a").first();
            if (a != null) {
                hashMap.put("blogUrl", a.attr("href").trim());
            }
        }*/

        Element menu2 = doc.getElementById("menu2");
        if (menu2 != null) {
            Element select = menu2.select("select").first();
            for (Element option : select.select("option")) {
                String url = option.attr("value");
                String name1 = option.text();
                //Log.e(mTag, name + " " + url);

                if (name1.equals(name)) {
                    hashMap.put(Config.SITE_ID_BLOG, url);
                    break;
                }
            }
        }

        hashMap.put("isOk", "ok");

        return hashMap;
    }

    public void parseBlog(String response, ArrayList<WebData> snsList) {
        response = clean(response);
        //Log.e(mTag, response);

        Document doc = Jsoup.parse(response);

        Element root = doc.getElementById("blogmodule");
        if (root != null) {
            for (Element li : root.select("li")) {

                Element a = li.select("a").first();
                if (a == null) {
                    continue;
                }
                String url = a.attr("href").trim();

                Element img = a.select("img").first();
                if (img == null) {
                    continue;
                }
                String imageUrl = img.attr("src").trim();

                Element el = li.select(".title").first();
                if (el == null) {
                    continue;
                }
                String title = el.text().trim();

                el = li.select(".date").first();
                if (el == null) {
                    continue;
                }
                String date = el.text().trim();

                el = li.select(".summary").first();
                if (el == null) {
                    continue;
                }
                String content = el.text().trim();

                //Log.e(mTag, title + " / " + date + " / " + content);

                WebData webData = new WebData();
                webData.setTitle(title);
                webData.setDate(date);
                webData.setContent(content);
                webData.setUrl(url);
                webData.setImageUrl(imageUrl);

                snsList.add(webData);
            }
        }
    }

    public void parseBlogList(String response, ArrayList<WebData> webDataList) {
        /*
        <div class="right2in" id="sheet">
            <div class="paginate">
                &nbsp;1&nbsp; |
                <a href="?p=2">&nbsp;2&nbsp;</a> |
                <a href="?p=3">&nbsp;3&nbsp;</a> |
                ...
                <a href="?p=15">&nbsp;15&nbsp;</a> |
                <a href="?p=2">&#65310;</a>
            </div>
            <h1 class="clearfix">
                <span class="date">
                    <span class="yearmonth">2016/03</span>
                    <span class="daydate">
                        <span class="dd1">29</span>
                        <span class="dd2">Tue</span>
                    </span>
                </span>
                <span class="heading">
                    <span class="author">若月佑美</span>
                    <span class="entrytitle">
                        <a href="http://blog.nogizaka46.com/yumi.wakatsuki/2016/03/031299.php" rel="bookmark">1週間も経ってるー(´,,•﹏• ,,｀)</a>
                    </span>
                </span>
            </h1>
            <div class="fkd"></div>
            <div class="entrybody">
                <div>髪のカラーの抜け方が早い。</div>
                <div>
                    <a href="http://dcimg.awalker.jp/img1.php?id=phu2qg0Vf9cBKA4BKCZQ81j2pg07yhJsTMg9A100AdAgpABNlZWV5e9V3A6FWvSiKCcZB7FvYxNFOLuocFWp8LYkc0XhC2u3t81pNarOGlyqtuPV6FCw1WKtIqwG3fT8BCsMfPZbULPaLQtvcFkbr2TRw01VSoafd3WxO6RpxMR70kahY7KwfuKzhg2jkR1Zx40M7Nx1">
                        <img src="http://img.nogizaka46.com/blog/yumi.wakatsuki/img/2016/03/29/8178961/0000.jpeg">
                    </a>
                </div>
            </div>
            <div class="entrybottom">
                2016/03/29 08:00｜
                <a href="http://blog.nogizaka46.com/yumi.wakatsuki/2016/03/031299.php">個別ページ</a>｜
                <a href="http://blog.nogizaka46.com/yumi.wakatsuki/2016/03/031299.php#comments">コメント(3)</a>
            </div>
            ...
        </div>
        */
        response = clean(response);
        //response = Util.getJapaneseString(response, "SHIFT-JIS");
        //Log.e(mTag, response);

        Document doc = Jsoup.parse(response);

        Element root = doc.getElementById("sheet");

        Elements h1s = root.select("h1");
        if (h1s == null) {
            return;
        }
        Elements entrybodys = root.select(".entrybody");
        if (entrybodys == null) {
            return;
        }

        //Log.e(mTag, "h1s.size(): " + h1s.size());
        //Log.e(mTag, "entrybodys.size(): " + entrybodys.size());

        for (int i = 0; i < h1s.size(); i++) {
            String id;
            String title;
            String name;
            String date;
            String content;
            String url;
            String thumbnailUrl = "";
            String imageUrl = "";

            Element el;

            Element h1 = h1s.get(i);

            el = h1.select(".yearmonth").first();
            if (el == null) {
                continue;
            }
            date = el.text().replace("/", "-");
            date += "-" + h1.select(".dd1").first().text();
            date += " " + h1.select(".dd2").first().text();

            name = h1.select(".author").first().text();

            el = h1.select(".entrytitle").first();
            el = el.select("a").first();
            title = el.text();
            url = el.attr("href");

            //if (i >= entrybodys.size()) {
            //    break;
            //}
            //Element entitybody = entrybodys.get(i);

            el = h1.nextElementSibling();
            Element entitybody = el.nextElementSibling();
            content = entitybody.text().trim();
            content = Util.removeSpace(content);
            //Log.e(mTag, content);

            for (Element img : entitybody.select("img")) {
                //Log.e(mTag, a.html());

                String src = img.attr("src");
                if (src.contains(".gif")) {
                    continue;
                }

                thumbnailUrl += src + "*";

                // 이미지 보호장치 있음 - 그냥 웹 뷰로 이동시킬 것
                // http://dcimg.awalker.jp/img1.php?id=phu2qg0Vf9cBKA4BKCZQ81j2pg07yhJsTMg9A100AdAgpABNlZWV5e9V3A6FWvSiKCcZB7FvYxNFOLuocFWp8LYkc0XhC2u3t81pNarOGlyqtuPV6FCw1WKtIqwG3fT8BCsMfPZbULPaLQtvcFkbr2TRw01VSoafd3WxO6RpxMR70kahY7KwfuKzhg2jkR1Zx40M7Nx1
                // http://dcimg.awalker.jp/img2.php?sec_key=phu2qg0Vf9cBKA4BKCZQ81j2pg07yhJsTMg9A100AdAgpABNlZWV5e9V3A6FWvSiKCcZB7FvYxNFOLuocFWp8LYkc0XhC2u3t81pNarOGlyqtuPV6FCw1WKtIqwG3fT8BCsMfPZbULPaLQtvcFkbr2TRw01VSoafd3WxO6RpxMR70kahY7KwfuKzhg2jkR1Zx40M7Nx1
                //imageUrl = imageUrl.replace("/img1.php?id=", "/img2.php?sec_key=");

                //el = img.parent();
                //if (!el.tagName().equals("a")) { // 큰 사진에는 링크가 걸려있음
                //    continue;
                //}
                imageUrl = el.attr("href") + "*";

                //boolean exist = false;
                //for (WebData webData : webDataList) {
                //    if (id.equals(webData.getGroupId())) {
                //        exist = true;
                //        break;
                //    }
                //}
            }

            if (!thumbnailUrl.isEmpty()) {
                thumbnailUrl = thumbnailUrl + "*";
                thumbnailUrl = thumbnailUrl.replace("**", "");

                imageUrl = imageUrl + "*";
                imageUrl = imageUrl.replace("**", "");
            }

            id = Util.urlToId(url);

            //Log.e(mTag, title + " / " + url + " / " + thumbnailUrl + " / " + imageUrl);

            //if (!exist) {
            WebData webData = new WebData();
            webData.setId(id);
            webData.setTitle(title);
            webData.setName(name);
            webData.setDate(date);
            webData.setContent(content);
            webData.setUrl(url);
            webData.setThumbnailUrl(thumbnailUrl);
            webData.setImageUrl(imageUrl);

            webDataList.add(webData);
        }
    }
}
