package com.summertaker.akb48guide.parser;

import com.summertaker.akb48guide.data.WebData;
import com.summertaker.akb48guide.util.Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Akb48ShopParser extends BaseParser {

    public void parseIndex(String response, ArrayList<WebData> dataList) {
        /*
        <div id="list_area">
            <h3>AKB48 2016年7月度 net shop限定個別生写真 「10th アニバーサリー」衣装 5枚セット</h3>
            <div class="block_body clearfix">
                <div class="product_item_wrap">
                    <div class="product_item clearfix">
                        <div class="productImage">
                            <div class="akb image_wrap">
                                <div class="soldout_list">
                                    <img src="//dwd7slh0nmufg.cloudfront.net/user_data/packages/default/img/icon/soldout_500.png">
                                </div>
                                <a href="/products/detail.php?product_id=43578&amp;akb48" >
                                    <img src="//dwd7slh0nmufg.cloudfront.net/img/products/AK-003-1607-23237_p01_160.jpg?1387605008" width=160px hight=160px alt="AKB48 2016年7月度 net shop限定個別生写真 「10th アニバーサリー」衣装 5枚セット 入山杏奈" class="title_icon" />
                				    <span class="itemIcon_team_akb">akb48</span>
                		        </a>
                            </div>
                            <div class="productContents">
                                <h4 class="heightLine">
                                    <a href="/products/detail.php?product_id=43578&amp;akb48" >AKB48 2016年7月度 net shop限定個別生写真 「10th アニバーサリー」衣装 ...</a>
                                </h4>
                                <p class="normal_price">
                                    ￥1,050
                                    <span class="normal tax">(税込)</span>
                                </p>
                            </div>
                        </div>
                    </div><!--product_item-->
                    ...
        */

        response = clean(response);
        //Log.e(mTag, response.substring(0, 100));

        Document doc = Jsoup.parse(response);
        Element root = doc.getElementById("undercolumn");

        for (Element row : root.select("#list_area")) {
            //Log.e(mTag, blog.html());

            String name;
            String url;
            String imageUrl;

            Element h3 = row.select("h3").first();
            name = h3.text();
            name = name.replace("AKB48", "").trim();

            Elements els = row.select(".productImage");
            int random = Util.getRandom(0, els.size() - 1);
            Element el = els.get(random);

            Element img = el.select(".title_icon").first();
            imageUrl = img.attr("src");
            imageUrl = "http:" + imageUrl;
            //Log.e(mTag, imageUrl);

            Element more = row.select(".btn_more").first();
            Element a = more.select("a").first();
            url = a.attr("href");
            url = "http://shopping.akb48-group.com" + url;

            //Log.e(mTag, url);

            WebData data = new WebData();
            data.setName(name);
            data.setUrl(url);
            data.setImageUrl(imageUrl);
            dataList.add(data);
        }
    }

    public void parseMobileIndex(String response, ArrayList<WebData> dataList) {
        /*
        <ul id="categorytreelist">
            <li class="level1 onmark">
                <span class="category_header"></span>
                <span class="category_body">
                    <a rel="external" href="/products/list.php?category_id=1841" class="onlink">
                        AKB48 2016年7月度 net shop限定個別生写真 「10th アニバーサリー」衣装 5枚セット
                    </a>
                </span>
                <ul>
                    <li class="level2">
                        <!--▼商品-->
                        <div class="list_area clearfix">
                            <!--★画像★-->
                            <p class="listphoto image_wrap">
                                <img src="//dwd7slh0nmufg.cloudfront.net/img/products/AK-003-1607-23237_p01_80.jpg?1387605008"  alt="AKB48 2016年7月度 net shop限定個別生写真 「10th アニバーサリー」衣装 5枚セット 入山杏奈" />
                                <span class="itemIcon_team_akb">akb48</span>
                                <span class="soldout">
                                    <img src="//dwd7slh0nmufg.cloudfront.net/user_data/packages/default/img/icon/soldout_160.png">
                                </span>
                            </p>
                        </div>
                    </li>
                    ...
         */
        response = clean(response);

        Document doc = Jsoup.parse(response);
        Element root = doc.getElementById("categorytreelist");

        if (root == null) {
            return;
        }

        for (Element li : root.select("li")) {
            //Log.e(mTag, blog.html());

            String name;
            String url;
            String imageUrl = "";

            Element el = li.select(".category_body").first();
            if (el == null) {
                continue;
            }

            Element a = el.select("a").first();
            if (a == null) {
                continue;
            }
            name = a.text();
            url = "http://shopping.akb48-group.com" + a.attr("href");

            Element ul = li.select("ul").first();
            if (ul == null) {
                continue;
            }
            for (Element li2 : ul.select("li")) {
                Element img = li2.select("img").first();
                if (img == null) {
                    continue;
                }
                String src = img.attr("src");
                //src = src.replace("_80.jpg", "_160.jpg");
                src = src.replace("_80.jpg", "_500.jpg");
                imageUrl += "http:" + src + "*";
            }
            //Log.e(mTag, imageUrl);

            WebData data = new WebData();
            data.setName(name);
            data.setUrl(url);
            data.setImageUrl(imageUrl);
            dataList.add(data);
        }
    }

    public String parseList(String response, String title, ArrayList<WebData> dataList) {
        /*
        <div id="list_area">
            <h3>AKB48 2016年7月度 net shop限定個別生写真 「10th アニバーサリー」衣装 5枚セット</h3>
            <form name="product_form43578" action="?" onsubmit="return false;">
                <input type="hidden" name="transactionid" value="bd91724e5c37152d55291fa8ffd4a39695dae165" />
            </form>
            <div class="block_body clearfix">
                <div class="product_item clearfix">
                    <div class="productImage">
                        <div class="akb image_wrap">
	    					<div class="soldout_list">
		    					<img src="//dwd7slh0nmufg.cloudfront.net/user_data/packages/default/img/icon/soldout_500.png">
			    			</div>
                            <a href="/products/detail.php?product_id=43578&amp;akb48" ><img src="//dwd7slh0nmufg.cloudfront.net/img/products/AK-003-1607-23237_p01_160.jpg?1387605008" width=160px hight=160px alt="AKB48 2016年7月度 net shop限定個別生写真 「10th アニバーサリー」衣装 5枚セット 入山杏奈" class="title_icon" />
			                <span class="itemIcon_team_akb">akb48</span></a>
                        </div>
                        <div class="productContents">
                            <h4 class="heightLine">
                                <a href="/products/detail.php?product_id=43578&amp;akb48" >AKB48 2016年7月度 net shop限定個別生写真 「10th アニバーサリー」衣装 ...</a>
                            </h4>
                            <p class="normal_price">￥1,050<span class="normal tax">(税込)</span></p>
                        </div>
                    </div>
                </div><!--product_item-->
                ...
        */

        String nextUrl = "";

        response = clean(response);
        //Log.e(mTag, response.substring(0, 100));

        Document doc = Jsoup.parse(response);
        Element root = doc.getElementById("list_area");

        if (root == null) {
            return nextUrl;
        }
        //Log.e(mTag, root.text().substring(100));

        for (Element div : root.select(".block_body")) {
            for (Element row : div.select(".product_item")) {
                dataList.add(parseItem(title, row));
            }
            for (Element row : div.select(".product_item_end")) {
                dataList.add(parseItem(title, row));
            }
        }

        /*
        <div id="pager">
            <div id="pagerWarp">
                <p class="all_items">72商品中</p>
                <p class="items">1-50商品</p>
                <p class="next">
                    <a href="?akb48&amp;category_id=1841&amp;pageno=2" onclick="fnNaviPage('2'); return false;">次のページ</a>
                </p>
                <div class="right_arrow">
                    <a href="?akb48&amp;category_id=1841&amp;pageno=2" onclick="fnNaviPage('2'); return false;">left_arrow</a>
                </div>
            </div>
        </div>
        */
        Element el = doc.getElementById("pager");
        if (el != null) {
            el = el.select(".next").first();
            if (el != null) {
                el = el.select("a").first();
                if (el != null) {
                    nextUrl = el.attr("href");
                    nextUrl = "http://shopping.akb48-group.com/products/list.php" + nextUrl;
                }
            }
        }

        return nextUrl;
    }

    public String parseMobileList(String response, String title, ArrayList<WebData> dataList) {
        /*
        <div id="list_area">
            <h3>AKB48 2016年7月度 net shop限定個別生写真 「10th アニバーサリー」衣装 5枚セット</h3>
            <form name="product_form43578" action="?" onsubmit="return false;">
                <input type="hidden" name="transactionid" value="bd91724e5c37152d55291fa8ffd4a39695dae165" />
            </form>
            <div class="block_body clearfix">
                <div class="product_item clearfix">
                    <div class="productImage">
                        <div class="akb image_wrap">
	    					<div class="soldout_list">
		    					<img src="//dwd7slh0nmufg.cloudfront.net/user_data/packages/default/img/icon/soldout_500.png">
			    			</div>
                            <a href="/products/detail.php?product_id=43578&amp;akb48" ><img src="//dwd7slh0nmufg.cloudfront.net/img/products/AK-003-1607-23237_p01_160.jpg?1387605008" width=160px hight=160px alt="AKB48 2016年7月度 net shop限定個別生写真 「10th アニバーサリー」衣装 5枚セット 入山杏奈" class="title_icon" />
			                <span class="itemIcon_team_akb">akb48</span></a>
                        </div>
                        <div class="productContents">
                            <h4 class="heightLine">
                                <a href="/products/detail.php?product_id=43578&amp;akb48" >AKB48 2016年7月度 net shop限定個別生写真 「10th アニバーサリー」衣装 ...</a>
                            </h4>
                            <p class="normal_price">￥1,050<span class="normal tax">(税込)</span></p>
                        </div>
                    </div>
                </div><!--product_item-->
                ...
        */

        String nextUrl = "";

        response = clean(response);
        //Log.e(mTag, response.substring(0, 100));

        Document doc = Jsoup.parse(response);
        Element root = doc.getElementById("product_list");

        if (root == null) {
            return nextUrl;
        }
        //Log.e(mTag, root.text().substring(100));

        for (Element row : root.select(".list_area")) {
            String name;
            String url;
            String imageUrl;

            Element img = row.select("img").first();

            String src = img.attr("src");
            src = src.replace("_80.jpg", "_500.jpg");
            imageUrl = "http:" + src;

            name = img.attr("alt");
            //Log.e(mTag, imageUrl);

            Element a = row.select("a").first();
            url = "http://shopping.akb48-group.com" + a.attr("href");

            //Log.e(mTag, name);

            WebData data = new WebData();
            data.setName(name);
            data.setUrl(url);
            data.setImageUrl(imageUrl);
            dataList.add(data);
        }

        /*
        <div id="pager">
            <div id="pagerWarp">
                <p class="all_items">72商品中</p>
                <p class="items">1-50商品</p>
                <p class="next">
                    <a href="?akb48&amp;category_id=1841&amp;pageno=2" onclick="fnNaviPage('2'); return false;">次のページ</a>
                </p>
                <div class="right_arrow">
                    <a href="?akb48&amp;category_id=1841&amp;pageno=2" onclick="fnNaviPage('2'); return false;">left_arrow</a>
                </div>
            </div>
        </div>
        */
        Element el = doc.getElementById("pager");
        if (el != null) {
            el = el.select(".next").first();
            if (el != null) {
                el = el.select("a").first();
                if (el != null) {
                    nextUrl = el.attr("href");
                    nextUrl = "http://shopping.akb48-group.com/products/list.php" + nextUrl;
                }
            }
        }

        return nextUrl;
    }

    public void parseDetail(String response, ArrayList<WebData> dataList) {
        /*
        <div id="detailphotobloc">
            <div class="photo image_wrap">
				<div class="soldout">
					<img src="//dwd7slh0nmufg.cloudfront.net/user_data/packages/default/img/icon/soldout_500.png">
				</div>
    		    <p><img src="//dwd7slh0nmufg.cloudfront.net/img/products/AK-003-1607-23237_p01_500.jpg?1387605008" width="500" height="500" alt="AKB48 2016年7月度 net shop限定個別生写真 「10th アニバーサリー」衣装 5枚セット 入山杏奈" id="MainPhoto" /></p>
                <ul class="thum_box mb10 mt10">
                    <li class="thum_photo">
                        <img src="//dwd7slh0nmufg.cloudfront.net/img/products/AK-003-1607-23237_p01_500.jpg?1387605008" width="90" alt="AKB48 2016年7月度 net shop限定個別生写真 「10th アニバーサリー」衣装 5枚セット 入山杏奈" class="ChangePhoto" onError="this.parentNode.removeChild(this)">
			  	    </li>
			  	    ...
                </ul>
            </div>
        	<!-- /photo -->
	    </div><!-- /detailphotobloc -->
        */

        response = clean(response);
        //Log.e(mTag, response.substring(0, 100));

        Document doc = Jsoup.parse(response);
        Element root = doc.getElementById("detailphotobloc");
        Element ul = root.select(".thum_box").first();

        for (Element row : ul.select("li")) {
            //Log.e(mTag, blog.html());

            String imageUrl;

            Element img = row.select("img").first();
            String src = img.attr("src");
            imageUrl = "http:" + src;

            //Log.e(mTag, imageUrl);

            WebData data = new WebData();
            data.setImageUrl(imageUrl);
            dataList.add(data);
        }
    }

    public String parseMobileDetail(String response, ArrayList<WebData> dataList) {
        response = clean(response);
        //Log.e(mTag, response.substring(0, 100));

        String result = "";

        Document doc = Jsoup.parse(response);

        Element error = doc.getElementById("product_none_block");
        if (error != null && !error.text().isEmpty()) {
            return error.text();
        }

        Element root = doc.getElementById("bx-pager");

        if (root == null) {
            return result;
        }

        for (Element row : root.select("a")) {
            //Log.e(mTag, blog.html());

            String imageUrl;

            Element img = row.select("img").first();
            String src = img.attr("src");
            imageUrl = "http:" + src;

            //Log.e(mTag, imageUrl);

            WebData data = new WebData();
            data.setImageUrl(imageUrl);
            dataList.add(data);
        }
        return result;
    }

    private WebData parseItem(String title, Element row) {
        String name;
        String nameOrg;
        String url;
        String imageUrl;

        Element el = row.select(".productImage").first();

        Element img = el.select(".title_icon").first();
        imageUrl = img.attr("src");
        imageUrl = "http:" + imageUrl;
        //Log.e(mTag, imageUrl);

        name = img.attr("alt");
        nameOrg = name;
        name = name.replace(title, "");
        name = name.replace("AKB48", "");
        name = name.replace("SKE48", "");
        name = name.replace("NMB48", "");
        name = name.replace("HKT48", "");
        name = name.trim();
        if (name.isEmpty()) {
            name = nameOrg;
        }

        el = row.select("h4").first();
        Element a = el.select("a").first();
        url = a.attr("href");
        url = "http://shopping.akb48-group.com" + url;

        //Log.e(mTag, title + " / " + url + " / " + date);

        WebData data = new WebData();
        data.setName(name);
        data.setContent(nameOrg);
        data.setUrl(url);
        data.setImageUrl(imageUrl);
        //dataList.add(data);

        return data;
    }
}
